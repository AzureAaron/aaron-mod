package net.azureaaron.mod.mixins;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.injected.AaronModItemMeta;
import net.azureaaron.mod.skyblock.item.SkyblockEnchantment;
import net.azureaaron.mod.skyblock.item.SkyblockEnchantments;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.ItemUtils;
import net.azureaaron.mod.utils.RomanNumerals;
import net.azureaaron.mod.utils.TextTransformer;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements AaronModItemMeta, ComponentHolder {
	@Unique
	private static final String STAR = "✪";
	@Unique
	private static final Predicate<String> HAS_MASTER_STAR = s -> s.contains("➊") || s.contains("➋") || s.contains("➌") || s.contains("➍") || s.contains("➎");
	@Unique
	private static final Style NO_ITALIC = Style.EMPTY.withItalic(false);

	@Shadow
	@Nullable
	public abstract <T> T set(ComponentType<? super T> type, @Nullable T value);

	@ModifyVariable(method = "getName", at = @At("STORE"))
	private Text aaronMod$customItemName(Text text) {
		if ((Functions.isOnHypixel() && Functions.isInSkyblock() || getAlwaysDisplaySkyblockInfo()) && (AaronModConfigManager.get().skyblock.dungeons.oldMasterStars || AaronModConfigManager.get().skyblock.dungeons.fancyDiamondHeadNames) && text != null) {
			String name = text.getString();
			Formatting masterStarStyle = Formatting.RED;

			if (!name.contains(STAR) || !HAS_MASTER_STAR.test(name)) return text;

			if (AaronModConfigManager.get().skyblock.dungeons.fancyDiamondHeadNames && isDiamondHead()) {
				Text styledName = TextTransformer.stylize(text, NO_ITALIC, "Diamond", Style.EMPTY.withColor(0x84dadd), 1);
				Text styledStars = TextTransformer.stylize(styledName, NO_ITALIC, STAR, Style.EMPTY.withColor(Formatting.AQUA), 5);

				if (!AaronModConfigManager.get().skyblock.dungeons.oldMasterStars) {
					String masterStar = switch (name) {
						case String s when s.contains("➊") -> "➊";
						case String s when s.contains("➋") -> "➋";
						case String s when s.contains("➌") -> "➌";
						case String s when s.contains("➍") -> "➍";
						case String s when s.contains("➎") -> "➎";

						default -> "?";
					};

					return TextTransformer.stylize(styledStars, NO_ITALIC, masterStar, Style.EMPTY.withColor(Formatting.DARK_AQUA), 1);
				} else {
					masterStarStyle = Formatting.DARK_AQUA;
					text = styledStars;
				}
			}

			if (AaronModConfigManager.get().skyblock.dungeons.oldMasterStars) {
				int masterStarsApplied = switch (name) {
					case String s when s.contains("➊") -> 1;
					case String s when s.contains("➋") -> 2;
					case String s when s.contains("➌") -> 3;
					case String s when s.contains("➍") -> 4;
					case String s when s.contains("➎") -> 5;

					default -> 0;
				};

				if (masterStarsApplied > 0) {
					Text newText = TextTransformer.recursiveCopy(text);
					ListIterator<Text> iterator = newText.getSiblings().listIterator();

					while (iterator.hasNext()) {
						Text component = iterator.next();
						String stringified = component.getString();

						//Swap the gold stars for the mixed red/gold stars
						if (stringified.contains(STAR)) {
							Text stars = Text.literal(STAR.repeat(masterStarsApplied)).setStyle(NO_ITALIC.withFormatting(masterStarStyle))
									.append((masterStarsApplied != 5) ? Text.literal(STAR.repeat(5 - masterStarsApplied)).setStyle(NO_ITALIC.withFormatting(Formatting.GOLD)) : Text.empty());

							iterator.set(stars);
						}

						//Hide the master star icon - we can't remove this otherwise if there is other text in this component then the name will be altered beyond what is necessary
						if (HAS_MASTER_STAR.test(stringified)) iterator.set(TextTransformer.withContent(component, stringified.substring(1)));
					}

					return newText;
				}
			}
		}

		return text;
	}

	@ModifyVariable(method = "appendComponentTooltip", at = @At("STORE"))
	private TooltipAppender aaronMod$rainbowifyMaxSkyblockEnchantments(TooltipAppender itemComponent) {
		if (AaronModConfigManager.get().skyblock.enchantments.rainbowMaxEnchants && ((Functions.isOnHypixel() && Functions.isInSkyblock()) || getAlwaysDisplaySkyblockInfo()) && itemComponent instanceof LoreComponent lore) {
			//Find what enchantments to replace with what colour
			NbtCompound appliedEnchantments = ItemUtils.getCustomData(this).getCompoundOrEmpty("enchantments");
			Object2IntMap<String> maxEnchantmentColours = new Object2IntOpenHashMap<>();
			Object2IntMap<String> goodEnchantmentColours = new Object2IntOpenHashMap<>();

			for (String id : appliedEnchantments.getKeys()) {
				SkyblockEnchantment enchantment = SkyblockEnchantments.getEnchantments().get(id);
				int level = appliedEnchantments.getInt(id, 0); //Will be 0 if the key isn't an int

				if (level > 0 && enchantment != null && enchantment.isAtGoodOrMaxLevel(level)) {
					//The name as shown when applied (e.g. Critical VII)
					String appliedName = enchantment.name() + " " + RomanNumerals.toRoman(level);

					if (enchantment.isAtMaxLevel(level)) {
						maxEnchantmentColours.put(appliedName, 0xAA5500);
					} else if (enchantment.isAtGoodLevel(level) && AaronModConfigManager.get().skyblock.enchantments.showGoodEnchants) {
						goodEnchantmentColours.put(appliedName, AaronModConfigManager.get().skyblock.enchantments.goodEnchantsColour.getRGB() & 0x00FFFFFF);
					}
				}
			}

			//Copy the text to ensure that we don't modify the original
			List<Text> lines = lore.lines().stream()
					.map(TextTransformer::recursiveCopy)
					.collect(Collectors.toList());

			for (Text line : lines) {
				Predicate<String> lineContains = line.getString()::contains;

				if (!maxEnchantmentColours.isEmpty() && maxEnchantmentColours.keySet().stream().anyMatch(lineContains)) {
					List<Text> textComponents = line.getSiblings();

					switch (AaronModConfigManager.get().skyblock.enchantments.rainbowMode) {
						case STATIC -> {
							int totalLength = 0;
							int positionLeftOffAt = 0;

							//Exclude non-max enchants from counting towards total length since it looks weird & incomplete otherwise
							for (Text currentComponent : textComponents) {
								String componentString = currentComponent.getString().trim();

								if (maxEnchantmentColours.containsKey(componentString) && currentComponent.getStyle().getColor().getRgb() == Formatting.BLUE.getColorValue()) {
									totalLength += componentString.length();
								}
							}

							ListIterator<Text> iterator = textComponents.listIterator();

							while (iterator.hasNext()) {
								Text currentComponent = iterator.next();
								String componentString = currentComponent.getString().trim();

								if (maxEnchantmentColours.containsKey(componentString) && currentComponent.getStyle().getColor().getRgb() == Formatting.BLUE.getColorValue()) {
									iterator.set(TextTransformer.progressivelyRainbowify(componentString, totalLength, positionLeftOffAt).styled(style -> style.withItalic(false)));
									maxEnchantmentColours.removeInt(componentString);
									positionLeftOffAt += componentString.length();
								}
							}
						}

						case CHROMA -> {
							for (Text currentComponent : textComponents) {
								String enchant = currentComponent.getString().trim();

								if (maxEnchantmentColours.containsKey(enchant) && currentComponent.getStyle().getColor().getRgb() == Formatting.BLUE.getColorValue()) {
									((MutableText) currentComponent).withColor(maxEnchantmentColours.getInt(enchant));
									maxEnchantmentColours.removeInt(enchant);
								}
							}
						}
					}
				}

				if (!goodEnchantmentColours.isEmpty() && goodEnchantmentColours.keySet().stream().anyMatch(lineContains)) {
					for (Text currentComponent : line.getSiblings()) {
						String enchant = currentComponent.getString().trim();

						if (goodEnchantmentColours.containsKey(enchant) && currentComponent.getStyle().getColor().getRgb() == Formatting.BLUE.getColorValue()) {
							((MutableText) currentComponent).withColor(goodEnchantmentColours.getInt(enchant));
							goodEnchantmentColours.removeInt(enchant);
						}
					}
					
				}
			}

			//Return a new lore component with the updated text
			//Note that the styledLines list is actively transformed as its read so we can't create this component ahead of time and modify the contents of that list (it won't work)
			return new LoreComponent(lines);
		}

		return itemComponent;
	}

	@Unique
	private boolean isDiamondHead() {
		String itemId = ItemUtils.getId(this);

		return itemId.equals("DIAMOND_BONZO_HEAD") || itemId.equals("DIAMOND_SCARF_HEAD") || itemId.equals("DIAMOND_PROFESSOR_HEAD") || itemId.equals("DIAMOND_THORN_HEAD") || itemId.equals("DIAMOND_LIVID_HEAD") || itemId.equals("DIAMOND_SADAN_HEAD") || itemId.equals("DIAMOND_NECRON_HEAD");
	}

	@Override
	public boolean getAlwaysDisplaySkyblockInfo() {
		return ItemUtils.getCustomData(this).getCompoundOrEmpty(Main.NAMESPACE).getBoolean("alwaysDisplaySkyblockInfo", false);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setAlwaysDisplaySkyblockInfo(boolean value) {
		NbtComponent component = contains(DataComponentTypes.CUSTOM_DATA) ? get(DataComponentTypes.CUSTOM_DATA) : NbtComponent.of(new NbtCompound());
		NbtCompound compound = component.getNbt();

		if (!compound.contains(Main.NAMESPACE)) {
			compound.put(Main.NAMESPACE, new NbtCompound());
		}

		compound.getCompoundOrEmpty(Main.NAMESPACE).putBoolean("alwaysDisplaySkyblockInfo", value);

		if (!contains(DataComponentTypes.CUSTOM_DATA)) {
			set(DataComponentTypes.CUSTOM_DATA, component);
		}
	}
}

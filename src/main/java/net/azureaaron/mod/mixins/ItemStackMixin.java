package net.azureaaron.mod.mixins;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
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
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipProvider;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements AaronModItemMeta, DataComponentHolder {
	@Unique
	private static final String STAR = "✪";
	@Unique
	private static final Predicate<String> HAS_MASTER_STAR = s -> s.contains("➊") || s.contains("➋") || s.contains("➌") || s.contains("➍") || s.contains("➎");
	@Unique
	private static final Style NO_ITALIC = Style.EMPTY.withItalic(false);

	@Shadow
	public abstract <T> @Nullable T set(DataComponentType<? super T> type, @Nullable T value);

	@ModifyVariable(method = "getHoverName", at = @At("STORE"))
	private Component aaronMod$customItemName(Component text) {
		if ((Functions.isOnHypixel() && Functions.isInSkyblock() || getAlwaysDisplaySkyblockInfo()) && (AaronModConfigManager.get().skyblock.dungeons.oldMasterStars || AaronModConfigManager.get().skyblock.dungeons.fancyDiamondHeadNames) && text != null) {
			String name = text.getString();
			ChatFormatting masterStarStyle = ChatFormatting.RED;

			if (!name.contains(STAR) || !HAS_MASTER_STAR.test(name)) return text;

			if (AaronModConfigManager.get().skyblock.dungeons.fancyDiamondHeadNames && isDiamondHead()) {
				Component styledName = TextTransformer.stylize(text, NO_ITALIC, "Diamond", Style.EMPTY.withColor(0x84DADD), 1);
				Component styledStars = TextTransformer.stylize(styledName, NO_ITALIC, STAR, Style.EMPTY.withColor(ChatFormatting.AQUA), 5);

				if (!AaronModConfigManager.get().skyblock.dungeons.oldMasterStars) {
					String masterStar = switch (name) {
						case String s when s.contains("➊") -> "➊";
						case String s when s.contains("➋") -> "➋";
						case String s when s.contains("➌") -> "➌";
						case String s when s.contains("➍") -> "➍";
						case String s when s.contains("➎") -> "➎";

						default -> "?";
					};

					return TextTransformer.stylize(styledStars, NO_ITALIC, masterStar, Style.EMPTY.withColor(ChatFormatting.DARK_AQUA), 1);
				} else {
					masterStarStyle = ChatFormatting.DARK_AQUA;
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
					Component newText = TextTransformer.recursiveCopy(text);
					ListIterator<Component> iterator = newText.getSiblings().listIterator();

					while (iterator.hasNext()) {
						Component component = iterator.next();
						String stringified = component.getString();

						//Swap the gold stars for the mixed red/gold stars
						if (stringified.contains(STAR)) {
							Component stars = Component.literal(STAR.repeat(masterStarsApplied)).setStyle(NO_ITALIC.applyFormat(masterStarStyle))
									.append((masterStarsApplied != 5) ? Component.literal(STAR.repeat(5 - masterStarsApplied)).setStyle(NO_ITALIC.applyFormat(ChatFormatting.GOLD)) : Component.empty());

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

	@ModifyVariable(method = "addToTooltip", at = @At("STORE"))
	private TooltipProvider aaronMod$rainbowifyMaxSkyblockEnchantments(TooltipProvider itemComponent) {
		if (AaronModConfigManager.get().skyblock.enchantments.rainbowMaxEnchants && ((Functions.isOnHypixel() && Functions.isInSkyblock()) || getAlwaysDisplaySkyblockInfo()) && itemComponent instanceof ItemLore lore) {
			//Find what enchantments to replace with what colour
			CompoundTag appliedEnchantments = ItemUtils.getCustomData(this).getCompoundOrEmpty("enchantments");
			Object2IntMap<String> maxEnchantmentColours = new Object2IntOpenHashMap<>();
			Object2IntMap<String> goodEnchantmentColours = new Object2IntOpenHashMap<>();

			for (String id : appliedEnchantments.keySet()) {
				SkyblockEnchantment enchantment = SkyblockEnchantments.getEnchantments().get(id);
				int level = appliedEnchantments.getIntOr(id, 0); //Will be 0 if the key isn't an int

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
			List<Component> lines = lore.lines().stream()
					.map(TextTransformer::recursiveCopy)
					.collect(Collectors.toList());

			for (Component line : lines) {
				Predicate<String> lineContains = line.getString()::contains;

				if (!maxEnchantmentColours.isEmpty() && maxEnchantmentColours.keySet().stream().anyMatch(lineContains)) {
					List<Component> textComponents = line.getSiblings();

					switch (AaronModConfigManager.get().skyblock.enchantments.rainbowMode) {
						case STATIC -> {
							int totalLength = 0;
							int positionLeftOffAt = 0;

							//Exclude non-max enchants from counting towards total length since it looks weird & incomplete otherwise
							for (Component currentComponent : textComponents) {
								String componentString = currentComponent.getString().trim();

								if (maxEnchantmentColours.containsKey(componentString) && currentComponent.getStyle().getColor().getValue() == ChatFormatting.BLUE.getColor()) {
									totalLength += componentString.length();
								}
							}

							ListIterator<Component> iterator = textComponents.listIterator();

							while (iterator.hasNext()) {
								Component currentComponent = iterator.next();
								String componentString = currentComponent.getString().trim();

								if (maxEnchantmentColours.containsKey(componentString) && currentComponent.getStyle().getColor().getValue() == ChatFormatting.BLUE.getColor()) {
									iterator.set(TextTransformer.progressivelyRainbowify(componentString, totalLength, positionLeftOffAt).withStyle(style -> style.withItalic(false)));
									maxEnchantmentColours.removeInt(componentString);
									positionLeftOffAt += componentString.length();
								}
							}
						}

						case CHROMA -> {
							for (Component currentComponent : textComponents) {
								String enchant = currentComponent.getString().trim();

								if (maxEnchantmentColours.containsKey(enchant) && currentComponent.getStyle().getColor().getValue() == ChatFormatting.BLUE.getColor()) {
									((MutableComponent) currentComponent).withColor(maxEnchantmentColours.getInt(enchant));
									maxEnchantmentColours.removeInt(enchant);
								}
							}
						}
					}
				}

				if (!goodEnchantmentColours.isEmpty() && goodEnchantmentColours.keySet().stream().anyMatch(lineContains)) {
					for (Component currentComponent : line.getSiblings()) {
						String enchant = currentComponent.getString().trim();

						if (goodEnchantmentColours.containsKey(enchant) && currentComponent.getStyle().getColor().getValue() == ChatFormatting.BLUE.getColor()) {
							((MutableComponent) currentComponent).withColor(goodEnchantmentColours.getInt(enchant));
							goodEnchantmentColours.removeInt(enchant);
						}
					}

				}
			}

			//Return a new lore component with the updated text
			//Note that the styledLines list is actively transformed as its read so we can't create this component ahead of time and modify the contents of that list (it won't work)
			return new ItemLore(lines);
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
		return ItemUtils.getCustomData(this).getCompoundOrEmpty(Main.NAMESPACE).getBooleanOr("alwaysDisplaySkyblockInfo", false);
	}

	@Override
	public void setAlwaysDisplaySkyblockInfo(boolean value) {
		CustomData component = has(DataComponents.CUSTOM_DATA) ? get(DataComponents.CUSTOM_DATA) : CustomData.of(new CompoundTag());
		CompoundTag compound = component.copyTag();

		if (!compound.contains(Main.NAMESPACE)) {
			compound.put(Main.NAMESPACE, new CompoundTag());
		}

		compound.getCompoundOrEmpty(Main.NAMESPACE).putBoolean("alwaysDisplaySkyblockInfo", value);

		if (!has(DataComponents.CUSTOM_DATA)) {
			set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
		}
	}
}

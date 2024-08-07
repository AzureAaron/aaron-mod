package net.azureaaron.mod.mixins;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.injected.AaronModItemMeta;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.Skyblock;
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
	private static final String[] MASTER_STARS = {"➊","➋","➌","➍","➎"};
	@Unique
	private static final String MASTER_STAR_REGEX = "➊|➋|➌|➍|➎";
	@Unique
	private static final Style BASE_STYLE = Style.EMPTY.withItalic(false);

	@Shadow
	public abstract <T> T set(ComponentType<? super T> type, @Nullable T value);

	@ModifyVariable(method = "getName", at = @At("STORE"))
	private Text aaronMod$customItemName(Text text) {
		if ((Functions.isOnHypixel() && Functions.isInSkyblock() || getAlwaysDisplaySkyblockInfo()) && (AaronModConfigManager.get().oldMasterStars || AaronModConfigManager.get().fancyDiamondHeads) && text != null) {
			String itemName = text.getString();

			if (AaronModConfigManager.get().fancyDiamondHeads && itemName.contains("Diamond") && itemName.contains("Head")) {
				Style nameStyle = Style.EMPTY.withColor(0x84dadd);
				Style starStyle = Style.EMPTY.withColor(Formatting.AQUA);
				Style masterStarStyle = Style.EMPTY.withColor(Formatting.DARK_AQUA);
				int masterStarsApplied = switch (itemName) {
					case String s when s.contains("➊") -> 1;
					case String s when s.contains("➋") -> 2;
					case String s when s.contains("➌") -> 3;
					case String s when s.contains("➍") -> 4;
					case String s when s.contains("➎") -> 5;

					default -> 0;
				};

				Text styledName = TextTransformer.stylize(text, BASE_STYLE, "Diamond", nameStyle, 1);
				Text styledStars = TextTransformer.stylize(styledName, BASE_STYLE, "✪", starStyle, 5);
				return TextTransformer.stylizeAndReplace(styledStars, BASE_STYLE, "✪", masterStarStyle, MASTER_STARS, MASTER_STAR_REGEX, "", masterStarsApplied);
			}

			if (AaronModConfigManager.get().oldMasterStars) {
				Style masterStarStyle = Style.EMPTY.withColor(Formatting.RED);
				int masterStarsApplied = switch (itemName) {
					case String s when s.contains("➊") -> 1;
					case String s when s.contains("➋") -> 2;
					case String s when s.contains("➌") -> 3;
					case String s when s.contains("➍") -> 4;
					case String s when s.contains("➎") -> 5;

					default -> 0;
				};

				return TextTransformer.stylizeAndReplace(text, BASE_STYLE, "✪", masterStarStyle, MASTER_STARS, MASTER_STAR_REGEX, "", masterStarsApplied);
			}
		}
		return text;
	}

	@ModifyVariable(method = "appendTooltip", at = @At("STORE"))
	private TooltipAppender aaronMod$rainbowifyMaxSkyblockEnchantments(TooltipAppender itemComponent) {
		if (AaronModConfigManager.get().rainbowifyMaxSkyblockEnchantments && ((Functions.isOnHypixel() && Functions.isInSkyblock()) || getAlwaysDisplaySkyblockInfo()) && itemComponent instanceof LoreComponent lore) {
			//For some reason the default styledLines list doesn't like when I modify a Text's siblings so we have to duplicate it
			//Modifying the style works fine for some reason, idk what even happens
			LoreComponent newLore = new LoreComponent(new ArrayList<>(lore.lines()), new ArrayList<>(lore.styledLines()));

			for (Text line : newLore.styledLines()) {
				if (Skyblock.getMaxEnchants().stream().anyMatch(line.getString()::contains)) {
					List<Text> textComponents = line.getSiblings();

					switch (AaronModConfigManager.get().rainbowifyMode) {
						case STATIC -> {
							int totalLength = 0;
							int positionLeftOffAt = 0;

							//Exclude non-max enchants from counting towards total length since it looks weird & incomplete otherwise
							for (Text currentComponent : textComponents) {
								String componentString = currentComponent.getString();

								if (Skyblock.getMaxEnchants().stream().anyMatch(componentString::contains)) totalLength += componentString.length();
							}

							for (int j = 0; j < textComponents.size(); j++) {
								String componentString = textComponents.get(j).getString();

								if (Skyblock.getMaxEnchants().stream().anyMatch(componentString::contains)) {
									textComponents.set(j, TextTransformer.progressivelyRainbowify(componentString, totalLength, positionLeftOffAt).styled(style -> style.withItalic(false)));
									positionLeftOffAt += componentString.length();
								}
							}
						}

						case DYNAMIC -> {
							for (Text currentComponent : textComponents) {
								String componentString = currentComponent.getString();

								if (Skyblock.getMaxEnchants().stream().anyMatch(componentString::contains)) {
									((MutableText) currentComponent).styled(style -> style.withColor(0xAA5500));
								}
							}
						}
					}
				}
			}
			return newLore;
		}
		return itemComponent;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean getAlwaysDisplaySkyblockInfo() {
		NbtComponent component = getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);

		return component.getNbt().getCompound(Main.NAMESPACE).getBoolean("alwaysDisplaySkyblockInfo");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setAlwaysDisplaySkyblockInfo(boolean value) {
		NbtComponent component = contains(DataComponentTypes.CUSTOM_DATA) ? get(DataComponentTypes.CUSTOM_DATA) : NbtComponent.of(new NbtCompound());
		NbtCompound compound = component.getNbt();

		if (!compound.contains(Main.NAMESPACE)) {
			compound.put(Main.NAMESPACE, new NbtCompound());
		}

		compound.getCompound(Main.NAMESPACE).putBoolean("alwaysDisplaySkyblockInfo", value);

		if (!contains(DataComponentTypes.CUSTOM_DATA)) {
			set(DataComponentTypes.CUSTOM_DATA, component);
		}
	}
}

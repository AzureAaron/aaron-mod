package net.azureaaron.mod.mixins;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.AaronModItemMeta;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Skyblock;
import net.azureaaron.mod.util.TextTransformer;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipAppender;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements AaronModItemMeta {
	private static final String[] AARONMOD$MASTER_STARS = {"➊","➋","➌","➍","➎"};
	private static final String AARONMOD$MASTER_STAR_REGEX = "➊|➋|➌|➍|➎";
	private static final Style AARONMOD$BASE_STYLE = Style.EMPTY.withItalic(false);

	@ModifyVariable(method = "getName", at = @At("STORE"))
	private Text aaronMod$customItemName(Text text) {
		if ((Functions.isOnHypixel() && Functions.isInSkyblock() || getAlwaysDisplaySkyblockInfo()) && (AaronModConfigManager.get().oldMasterStars || AaronModConfigManager.get().fancyDiamondHeads) && text != null) {
			String itemName = text.getString();

			if (AaronModConfigManager.get().fancyDiamondHeads && itemName.contains("Diamond") && itemName.contains("Head")) {
				Style nameStyle = Style.EMPTY.withColor(0x84dadd);
				Style starStyle = Style.EMPTY.withColor(Formatting.AQUA);
				Style masterStarStyle = Style.EMPTY.withColor(Formatting.DARK_AQUA);
				int masterStarsApplied = 0;

				if (itemName.contains("➊")) masterStarsApplied = 1;
				if (itemName.contains("➋")) masterStarsApplied = 2;
				if (itemName.contains("➌")) masterStarsApplied = 3;
				if (itemName.contains("➍")) masterStarsApplied = 4;
				if (itemName.contains("➎")) masterStarsApplied = 5;

				Text styledName = TextTransformer.stylize(text, AARONMOD$BASE_STYLE, "Diamond", nameStyle, 1);
				Text styledStars = TextTransformer.stylize(styledName, AARONMOD$BASE_STYLE, "✪", starStyle, 5);
				return TextTransformer.stylizeAndReplace(styledStars, AARONMOD$BASE_STYLE, "✪", masterStarStyle, AARONMOD$MASTER_STARS, AARONMOD$MASTER_STAR_REGEX, "", masterStarsApplied);
			}

			if (AaronModConfigManager.get().oldMasterStars) {
				Style masterStarStyle = Style.EMPTY.withColor(Formatting.RED);
				int masterStarsApplied = 0;

				if (itemName.contains("➊")) masterStarsApplied = 1;
				if (itemName.contains("➋")) masterStarsApplied = 2;
				if (itemName.contains("➌")) masterStarsApplied = 3;
				if (itemName.contains("➍")) masterStarsApplied = 4;
				if (itemName.contains("➎")) masterStarsApplied = 5;

				return TextTransformer.stylizeAndReplace(text, AARONMOD$BASE_STYLE, "✪", masterStarStyle, AARONMOD$MASTER_STARS, AARONMOD$MASTER_STAR_REGEX, "", masterStarsApplied);
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
}

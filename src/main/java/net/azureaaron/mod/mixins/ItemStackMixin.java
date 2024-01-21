package net.azureaaron.mod.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Skyblock;
import net.azureaaron.mod.util.TextTransformer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow private NbtCompound nbt;
	
	@Shadow public abstract boolean hasCustomName();
	@Shadow public abstract boolean hasEnchantments();
	@Shadow public abstract boolean hasNbt();
	
	private static final String[] AARONMOD$MASTER_STARS = {"➊","➋","➌","➍","➎"};
	private static final String AARONMOD$MASTER_STAR_REGEX = "➊|➋|➌|➍|➎";
	private static final Style AARONMOD$BASE_STYLE = Style.EMPTY.withItalic(false);
	
	@ModifyVariable(method = "getName", at = @At("STORE"))
	private Text aaronMod$customItemName(Text text) {
		if((Functions.isOnHypixel() && Functions.isInSkyblock()) || aaronMod$shouldApplyEffect() && (AaronModConfigManager.get().oldMasterStars || AaronModConfigManager.get().fancyDiamondHeads)) {
			String itemName = text.getString();
			
			if(AaronModConfigManager.get().fancyDiamondHeads && itemName.contains("Diamond") && itemName.contains("Head")) {
				Style nameStyle = Style.EMPTY.withColor(0x84dadd);
				Style starStyle = Style.EMPTY.withColor(Formatting.AQUA);
				Style masterStarStyle = Style.EMPTY.withColor(Formatting.DARK_AQUA);
				int masterStarsApplied = 0;
				
				if(itemName.contains("➊")) masterStarsApplied = 1;
				if(itemName.contains("➋")) masterStarsApplied = 2;
				if(itemName.contains("➌")) masterStarsApplied = 3;
				if(itemName.contains("➍")) masterStarsApplied = 4;
				if(itemName.contains("➎")) masterStarsApplied = 5;
				
				Text styledName = TextTransformer.stylize(text, AARONMOD$BASE_STYLE, "Diamond", nameStyle, 1);
				Text styledStars = TextTransformer.stylize(styledName, AARONMOD$BASE_STYLE, "✪", starStyle, 5);
				return TextTransformer.stylizeAndReplace(styledStars, AARONMOD$BASE_STYLE, "✪", masterStarStyle, AARONMOD$MASTER_STARS, AARONMOD$MASTER_STAR_REGEX, "", masterStarsApplied);
			}
			
			if(AaronModConfigManager.get().oldMasterStars) {
				Style masterStarStyle = Style.EMPTY.withColor(Formatting.RED);
				int masterStarsApplied = 0;
				
				if(itemName.contains("➊")) masterStarsApplied = 1;
				if(itemName.contains("➋")) masterStarsApplied = 2;
				if(itemName.contains("➌")) masterStarsApplied = 3;
				if(itemName.contains("➍")) masterStarsApplied = 4;
				if(itemName.contains("➎")) masterStarsApplied = 5;
				
				return TextTransformer.stylizeAndReplace(text, AARONMOD$BASE_STYLE, "✪", masterStarStyle, AARONMOD$MASTER_STARS, AARONMOD$MASTER_STAR_REGEX, "", masterStarsApplied);
			}
		}
		return text;
	}
	
	/*@Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, remap = false, shift = At.Shift.AFTER))
	private void aaronMod$showItemGroupsInSurvival(@Local(ordinal = 0) List<Text> tooltip) {
		ItemStack stack = (ItemStack) ((Object) this);
		MinecraftClient client = MinecraftClient.getInstance();
		//Categories only show in creative if the stack doesn't have any custom nbt (excluding the damage value if its 0) - maybe try to match this functionality better sometime down the road
		if(client.player != null && !client.player.isCreative() && Config.showItemGroupsOutsideOfCreative && !this.hasCustomName() && !this.hasEnchantments() && !this.hasNbt()) {
			int count = 1;
			for(ItemGroup group : ItemGroups.getGroupsToDisplay()) {
				if(group.getType() == ItemGroup.Type.SEARCH || !group.contains(stack)) continue;
				tooltip.add(count++, group.getDisplayName().copy().formatted(Formatting.BLUE));
			}
		}
	}*/
	
	@ModifyVariable(method = "getTooltip", at = @At("STORE"), ordinal = 1)
	private MutableText aaronMod$rainbowifyMaxSkyblockEnchantments(MutableText text) {
		if(AaronModConfigManager.get().rainbowifyMaxSkyblockEnchantments && ((Functions.isOnHypixel() && Functions.isInSkyblock()) || aaronMod$shouldApplyEffect()) && Skyblock.MAX_LEVEL_ENCHANTMENTS.stream().anyMatch(text.getString()::contains)) {
			MutableText newText = Text.empty().styled(style -> style.withItalic(false));
			List<Text> textComponents = text.getSiblings();
			
			if(AaronModConfigManager.get().rainbowifyMode == AaronModConfig.RainbowifyMode.STATIC) {
				int totalLength = 0;
				int positionLeftOffAt = 0;
				
				//Exclude non-max enchants from counting towards total length since it looks weird & incomplete otherwise
				for(int i = 0; i < textComponents.size(); i++) {
					String componentString = textComponents.get(i).getString();
					if(Skyblock.MAX_LEVEL_ENCHANTMENTS.stream().anyMatch(componentString::contains)) totalLength += componentString.length();
				}
							
				for(int i = 0; i < textComponents.size(); i++) {
					Text currentComponent = textComponents.get(i);
					String componentString = currentComponent.getString();
					if(Skyblock.MAX_LEVEL_ENCHANTMENTS.stream().anyMatch(componentString::contains)) {
						newText.append(TextTransformer.progressivelyRainbowify(componentString, totalLength, positionLeftOffAt));
						positionLeftOffAt += componentString.length();
						continue;
					}
					
					newText.append(currentComponent);
				}
			}
			
			if(AaronModConfigManager.get().rainbowifyMode == AaronModConfig.RainbowifyMode.DYNAMIC) {
				for(int i = 0; i < textComponents.size(); i ++) {
					String componentString = textComponents.get(i).getString();
					if(Skyblock.MAX_LEVEL_ENCHANTMENTS.stream().anyMatch(componentString::contains)) {
						newText.append(Text.literal(componentString).styled(style -> style.withColor(0xAA5500)));
						continue;
					}
					
					newText.append(textComponents.get(i));
				}
			}
			return newText;
		}
		return text;
	}
	
	private boolean aaronMod$shouldApplyEffect() {
		return nbt != null && nbt.contains("aaronModAlwaysDisplaySBStuff") && nbt.getBoolean("aaronModAlwaysDisplaySBStuff");
	}
}

package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;

@Mixin(PotionItem.class)
public class PotionItemMixin extends Item {

	public PotionItemMixin(Settings settings) {
		super(settings);
	}
	
	@Override
	public boolean hasGlint(ItemStack stack) {
		return AaronModConfigManager.get().shinyPotions && (super.hasGlint(stack) || !PotionUtil.getPotionEffects(stack).isEmpty());
	}
}

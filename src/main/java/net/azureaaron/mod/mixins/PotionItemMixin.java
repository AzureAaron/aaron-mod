package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;

@Mixin(PotionItem.class)
public class PotionItemMixin extends Item {

	public PotionItemMixin(Properties settings) {
		super(settings);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return AaronModConfigManager.get().uiAndVisuals.legacyRevival.potionGlint && (super.isFoil(stack) || stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).hasEffects());
	}
}

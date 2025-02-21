package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;

@Mixin(PotionItem.class)
public class PotionItemMixin extends Item {

	public PotionItemMixin(Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return AaronModConfigManager.get().uiAndVisuals.legacyRevival.potionGlint && (super.hasGlint(stack) || stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).hasEffects());
	}
}

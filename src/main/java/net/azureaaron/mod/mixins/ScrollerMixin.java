package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.input.Scroller;

@Mixin(Scroller.class)
public class ScrollerMixin {

	@ModifyVariable(method = "scrollCycling", at = @At(value = "STORE", ordinal = 2), argsOnly = true, ordinal = 0)
	private static int aaronMod$modifyUpScroll(int newSlot) {
		return !AaronModConfigManager.get().refinements.input.disableScrollLooping ? newSlot : 0;
	}

	@ModifyVariable(method = "scrollCycling", at = @At(value = "STORE", ordinal = 3), argsOnly = true, ordinal = 0)
	private static int aaronMod$modifyDownScroll(int newSlot, @Local(argsOnly = true, ordinal = 1) int total) {
		return !AaronModConfigManager.get().refinements.input.disableScrollLooping ? newSlot : total - 1;
	}
}

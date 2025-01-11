package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@ModifyExpressionValue(method = "getHandSwingDuration", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectUtil;hasHaste(Lnet/minecraft/entity/LivingEntity;)Z"))
	private boolean aaronMod$ignoreHaste(boolean original) {
		return AaronModConfigManager.get().itemModelCustomization.enableItemModelCustomization && AaronModConfigManager.get().itemModelCustomization.ignoreHaste ? false : original;
	}

	@ModifyExpressionValue(method = "getHandSwingDuration", at = @At(value = "CONSTANT", args = "intValue=6"))
	private int aaronMod$modifySwingDuration(int original) {
		return AaronModConfigManager.get().itemModelCustomization.enableItemModelCustomization ? AaronModConfigManager.get().itemModelCustomization.swingDuration : original;
	}
}

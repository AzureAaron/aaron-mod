package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.world.level.BaseSpawner;

@Mixin(BaseSpawner.class)
public class MobSpawnerLogicMixin {

	@Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
	private void aaronMod$dontTickClientLogic(CallbackInfo ci) {
		if (AaronModConfigManager.get().uiAndVisuals.world.hideMobSpawnerAnimations) ci.cancel();
	}
}

package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.Config;
import net.minecraft.world.MobSpawnerLogic;

@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin {

	@Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
	private void aaronMod$dontTickClientLogic(CallbackInfo ci) {
		if(Config.hideSpinningMobInMobSpawner) ci.cancel();
	}
}

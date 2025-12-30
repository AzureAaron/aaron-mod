package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;

@Mixin(LightningBoltRenderer.class)
public class LightningBoltRendererMixin {

	@Inject(method = "submit", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideLightningBolt(CallbackInfo ci) {
		if (AaronModConfigManager.get().uiAndVisuals.world.hideLightning) ci.cancel();
	}
}

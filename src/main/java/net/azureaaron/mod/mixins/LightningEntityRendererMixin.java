package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.render.entity.LightningEntityRenderer;

@Mixin(LightningEntityRenderer.class)
public class LightningEntityRendererMixin {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideLightningBolt(CallbackInfo ci) {
		if (AaronModConfigManager.get().uiAndVisuals.world.hideLightning) ci.cancel();
	}
}

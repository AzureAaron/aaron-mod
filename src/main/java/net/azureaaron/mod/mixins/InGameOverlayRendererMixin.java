package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.renderer.ScreenEffectRenderer;

@Mixin(ScreenEffectRenderer.class)
public class InGameOverlayRendererMixin {

	@Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
	private static void aaronMod$hideFireOverlay(CallbackInfo ci) {
		if (AaronModConfigManager.get().uiAndVisuals.overlays.hideFireOverlay) ci.cancel();
	}
}

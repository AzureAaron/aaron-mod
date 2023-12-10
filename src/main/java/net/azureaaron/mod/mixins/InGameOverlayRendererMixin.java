package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
	
	@Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
	private static void aaronMod$hideFireOverlay(CallbackInfo ci) {
		if(AaronModConfigManager.get().hideFireOverlay) ci.cancel();
	}
}

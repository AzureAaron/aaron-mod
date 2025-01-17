package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.injected.ScreenResizeMarker;
import net.minecraft.client.gui.screen.ReconfiguringScreen;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public class ScreenMixin implements ScreenResizeMarker {
	@Unique
	private boolean screenResized;

	@Override
	public void markResized(boolean resized) {
		this.screenResized = resized;
	}

	@Override
	public boolean wasResized() {
		return this.screenResized;
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideReconfiguringScreen(CallbackInfo ci) {
		if ((Object) this instanceof ReconfiguringScreen && AaronModConfigManager.get().hideWorldLoadingScreen) ci.cancel();
	}
}

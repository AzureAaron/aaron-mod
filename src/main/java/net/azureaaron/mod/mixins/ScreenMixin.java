package net.azureaaron.mod.mixins;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.injected.ScreenResizeMarker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ReconfiguringScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.util.InputUtil;

@Mixin(Screen.class)
public class ScreenMixin implements ScreenResizeMarker {
	@Unique
	private boolean screenResized;
	@Shadow
	protected MinecraftClient client;

	@Override
	public void markResized(boolean resized) {
		this.screenResized = resized;
	}

	@Override
	public boolean wasResized() {
		return this.screenResized;
	}

	@Inject(method = "init(II)V", at = @At("TAIL"))
	private void aaronMod$hideCursor(CallbackInfo ci) {
		Object instance = (Object) this;

		if ((instance instanceof LevelLoadingScreen || instance instanceof ReconfiguringScreen) && AaronModConfigManager.get().uiAndVisuals.world.hideWorldLoadingScreen) {
			//Prevents the mouse from being movable while we cancel the rendering of the screen
			InputUtil.setCursorParameters(this.client.getWindow(), GLFW.GLFW_CURSOR_DISABLED, this.client.mouse.getX(), this.client.mouse.getY());
		}
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideReconfiguringScreen(CallbackInfo ci) {
		if ((Object) this instanceof ReconfiguringScreen && AaronModConfigManager.get().uiAndVisuals.world.hideWorldLoadingScreen) ci.cancel();
	}
}

package net.azureaaron.mod.mixins;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.platform.InputConstants;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.injected.ScreenResizeMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;

@Mixin(Screen.class)
public class ScreenMixin implements ScreenResizeMarker {
	@Unique
	private boolean screenResized;
	@Shadow
	protected Minecraft minecraft;

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

		if ((instance instanceof LevelLoadingScreen || instance instanceof ServerReconfigScreen) && AaronModConfigManager.get().uiAndVisuals.world.hideWorldLoadingScreen) {
			//Prevents the mouse from being movable while we cancel the rendering of the screen
			InputConstants.grabOrReleaseMouse(this.minecraft.getWindow(), GLFW.GLFW_CURSOR_DISABLED, this.minecraft.mouseHandler.xpos(), this.minecraft.mouseHandler.ypos());
		}
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideReconfiguringScreen(CallbackInfo ci) {
		if ((Object) this instanceof ServerReconfigScreen && AaronModConfigManager.get().uiAndVisuals.world.hideWorldLoadingScreen) ci.cancel();
	}
}

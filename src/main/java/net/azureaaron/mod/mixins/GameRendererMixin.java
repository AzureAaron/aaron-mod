package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.azureaaron.mod.Keybinds;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.render.TimeUniform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	
	@Shadow @Final MinecraftClient client;
	private boolean aaronMod$cameraSmoothed = false;
	
	@ModifyReturnValue(method = "getFov", at = @At("RETURN"))
	private double aaronMod$zoom(double fov) {
		if (Keybinds.zoomKeybind.isPressed()) {
			if (!this.aaronMod$cameraSmoothed) { 
				this.aaronMod$cameraSmoothed = true; 
				this.client.options.smoothCameraEnabled = true; 
			}
			
			return fov * AaronModConfigManager.get().zoomMultiplier;
		} else {
			if (this.aaronMod$cameraSmoothed) {
				this.aaronMod$cameraSmoothed = false;
				this.client.options.smoothCameraEnabled = false;
			}
		}
		return fov;
	}
	
	@Inject(method = "render", at = @At("HEAD"))
	private void aaronMod$timeUniform(CallbackInfo ci) {
		TimeUniform.updateShaderTime();
	}
}

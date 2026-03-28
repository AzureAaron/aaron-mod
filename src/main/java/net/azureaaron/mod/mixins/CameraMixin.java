package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.azureaaron.mod.Keybinds;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

@Mixin(Camera.class)
public class CameraMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	@Unique
	private boolean cameraSmoothed = false;

	@ModifyReturnValue(method = "calculateFov", at = @At("RETURN"))
	private float aaronMod$zoom(float fov) {
		if (Keybinds.zoomKeybind.isDown()) {
			if (!this.cameraSmoothed) {
				this.cameraSmoothed = true;
				this.minecraft.options.smoothCamera = true;
			}

			return (float) (fov * AaronModConfigManager.get().uiAndVisuals.world.zoomMultiplier);
		} else if (this.cameraSmoothed) {
			this.cameraSmoothed = false;
			this.minecraft.options.smoothCamera = false;
		}

		return fov;
	}
}

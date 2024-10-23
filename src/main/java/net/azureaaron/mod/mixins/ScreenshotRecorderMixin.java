package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;

@Mixin(ScreenshotRecorder.class)
public class ScreenshotRecorderMixin {

	@ModifyReturnValue(method = "takeScreenshot", at = @At("RETURN"))
	private static NativeImage aaronMod$markScreenshotImages(NativeImage original) {
		original.markScreenshot(true);

		return original;
	}
}

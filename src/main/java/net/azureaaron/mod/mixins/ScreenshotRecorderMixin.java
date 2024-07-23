package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImage.Format;
import net.minecraft.client.util.ScreenshotRecorder;

@Mixin(ScreenshotRecorder.class)
public class ScreenshotRecorderMixin {

	@WrapOperation(method = "takeScreenshot", at = @At(value = "NEW", target = "Lnet/minecraft/client/texture/NativeImage;"))
	private static NativeImage aaronMod$noAlphaChannel(int width, int height, boolean useStb, Operation<NativeImage> operation) {
		return AaronModConfigManager.get().optimizedScreenshots ? new NativeImage(Format.RGB, width, height, useStb) : operation.call(width, height, useStb);
	}
}

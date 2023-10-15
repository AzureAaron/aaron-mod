package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.azureaaron.mod.Config;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImage.Format;
import net.minecraft.client.util.ScreenshotRecorder;

@Mixin(ScreenshotRecorder.class)
public class ScreenshotRecorderMixin {

	@Redirect(method = "takeScreenshot", at = @At(value = "NEW", target = "Lnet/minecraft/client/texture/NativeImage;"))
	private static NativeImage aaronMod$noAlphaChannel(int width, int height, boolean useStb) {
		return new NativeImage(Config.optimizedScreenshots ? Format.RGB : Format.RGBA, width, height, useStb);
	}
}

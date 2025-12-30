package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Screenshot;

@Mixin(Screenshot.class)
public class ScreenshotRecorderMixin {

	@Inject(method = "method_68156", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
	private static void aaronMod$markScreenshotImages(CallbackInfo ci, @Local NativeImage image) {
		image.markScreenshot();
	}
}

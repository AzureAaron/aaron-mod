package net.azureaaron.mod.mixins;

import java.io.IOException;
import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.lwjgl.stb.STBImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.ImagePreview;
import net.azureaaron.mod.injected.NativeImageMarker;
import net.azureaaron.mod.utils.ImageMetadata;
import net.minecraft.client.texture.NativeImage;

@Mixin(NativeImage.class)
public class NativeImageMixin implements NativeImageMarker {
	@Unique
	private static final Function<StackFrame, Class<?>> GET_CLASS = StackFrame::getDeclaringClass;
	@Unique
	private static final Predicate<Class<?>> IMAGE_PREVIEW_MATCH = ImagePreview.class::equals;

	@Unique
	private boolean isScreenshot;

	@Unique
	private static Class<?>[] findCallerClasses(Stream<StackFrame> stackFrames) {
		return stackFrames.map(GET_CLASS).toArray(Class<?>[]::new);
	}

	@WrapOperation(method = "read(Lnet/minecraft/client/texture/NativeImage$Format;Ljava/nio/ByteBuffer;)Lnet/minecraft/client/texture/NativeImage;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PngMetadata;validate(Ljava/nio/ByteBuffer;)V"))
	private static void aaronMod$extendedImageTypeValidationForImagePreview(ByteBuffer buffer, Operation<Void> operation) throws IOException {
		Class<?>[] callerClasses = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).walk(NativeImageMixin::findCallerClasses);
		boolean wasCalledByImagePreview = Arrays.stream(callerClasses).anyMatch(IMAGE_PREVIEW_MATCH);

		if (wasCalledByImagePreview) {
			boolean isGif = ImageMetadata.validateGif(buffer);
			boolean isPng = ImageMetadata.validatePng(buffer);
			boolean isJpeg = ImageMetadata.validateJpeg(buffer);

			if (isGif || !(isPng || isJpeg)) {
				throw new IOException("Image is either a GIF or isn't a PNG or a JPEG!");
			}
		} else {
			operation.call(buffer);
		}
	}

	//https://github.com/nothings/stb/blob/master/stb_image_write.h#L79
	@ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lorg/lwjgl/stb/STBImageWrite;nstbi_write_png_to_func(JJIIIJI)I", remap = false), index = 4)
	private int aaronMod$noAlphaChannel(int comp) {
		return AaronModConfigManager.get().optimizedScreenshots && this.isScreenshot ? STBImage.STBI_rgb : comp;
	}

	@Override
	public void markScreenshot(boolean isScreenshot) {
		this.isScreenshot = isScreenshot;
	}
}

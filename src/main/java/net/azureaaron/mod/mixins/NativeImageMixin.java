package net.azureaaron.mod.mixins;

import java.io.IOException;
import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.jtracy.MemoryPool;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.ImagePreview;
import net.azureaaron.mod.injected.NativeImageMarker;
import net.azureaaron.mod.utils.ImageMetadata;

@Mixin(NativeImage.class)
public abstract class NativeImageMixin implements NativeImageMarker {
	@Unique
	private static final Function<StackFrame, Class<?>> GET_CLASS = StackFrame::getDeclaringClass;
	@Unique
	private static final Predicate<Class<?>> IMAGE_PREVIEW_MATCH = ImagePreview.class::equals;
	/**
	 * Also the same as the NativeImage Format's value for the RGB channel count
	 */
	@Unique
	private static final int RGB_CHANNEL_COUNT = STBImage.STBI_rgb;
	@Shadow
	@Final
	private static Logger LOGGER;
	@Shadow
	@Final
	private static MemoryPool MEMORY_POOL;

	@Shadow
	@Final
	private int width;
	@Shadow
	@Final
	private int height;
	@Unique
	private boolean isScreenshot;

	@Shadow
	public abstract int[] getPixelsABGR();

	@Unique
	private static Class<?>[] findCallerClasses(Stream<StackFrame> stackFrames) {
		return stackFrames.map(GET_CLASS).toArray(Class<?>[]::new);
	}

	@WrapOperation(method = "read(Lcom/mojang/blaze3d/platform/NativeImage$Format;Ljava/nio/ByteBuffer;)Lcom/mojang/blaze3d/platform/NativeImage;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PngInfo;validateHeader(Ljava/nio/ByteBuffer;)V"))
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

	@WrapMethod(method = "writeToChannel")
	private boolean aaronMod$writeWithoutAlphaChannel(WritableByteChannel channel, Operation<Boolean> operation) throws IOException {
		if (AaronModConfigManager.get().refinements.screenshots.optimizedScreenshots && this.isScreenshot) {
			int pixels = this.width * this.height;
			long size = pixels * RGB_CHANNEL_COUNT;
			long pointer = MemoryUtil.nmemAlloc(size);
			MEMORY_POOL.malloc(pointer, (int) size);

			//The allocation was successful
			if (pointer != MemoryUtil.NULL) {
				int[] originalPixels = this.getPixelsABGR();

				//Copy pixels from the original image in the ABGR format while dropping the alpha from the pixels
				for (int i = 0; i < pixels; i++) {
					long stride = (long) i * (long) RGB_CHANNEL_COUNT;

					MemoryUtil.memPutInt(pointer + stride, originalPixels[i] & 0x00FFFFFF);
				}

				NativeImage.WriteCallback writeCallback = new NativeImage.WriteCallback(channel);

				//Writing the image
				try {
					int cappedHeight = Math.min(this.height, Integer.MAX_VALUE / this.width / RGB_CHANNEL_COUNT);

					//Warn about the height being dropped
					if (cappedHeight < this.height) {
						LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", this.height, cappedHeight);
					}

					//Write the image with STB
					if (STBImageWrite.nstbi_write_png_to_func(writeCallback.address(), 0L, this.width, cappedHeight, RGB_CHANNEL_COUNT, pointer, 0) != 0) {
						writeCallback.throwIfException();

						return true;
					}
				} finally {
					writeCallback.free();
					MemoryUtil.nmemFree(pointer);
					MEMORY_POOL.free(pointer);
				}
			}

			return operation.call(channel);
		} else {
			return operation.call(channel);
		}
	}

	@Override
	public void markScreenshot() {
		this.isScreenshot = true;
	}
}

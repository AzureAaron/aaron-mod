package net.azureaaron.mod.mixins;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import net.azureaaron.mod.features.ImagePreview;
import net.minecraft.client.texture.NativeImage;

@Mixin(NativeImage.class)
public class NativeImageMixin {
	private static final Function<StackFrame, Class<?>> GET_CLASS = frame -> frame.getDeclaringClass();
	private static final Predicate<Class<?>> IMAGE_PREVIEW_MATCH = ImagePreview.class::equals;
	
	private static Class<?>[] findCallerClasses(Stream<StackFrame> stackFrames) {
		return stackFrames.map(GET_CLASS).toArray(Class<?>[]::new);
	}
	
	//This restriction is the dumbest shit ever - NativeImage works perfectly fine with JPGs and even GIFs
	@WrapWithCondition(method = "read(Lnet/minecraft/client/texture/NativeImage$Format;Ljava/nio/ByteBuffer;)Lnet/minecraft/client/texture/NativeImage;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PngMetadata;validate(Ljava/nio/ByteBuffer;)V"))
	private static boolean aaronMod$ignoreFileTypeCheckWhenItsFromImagePreview(ByteBuffer buffer) {
		Class<?>[] callerClasses = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).walk(NativeImageMixin::findCallerClasses);
		boolean wasCalledByImagePreview = Arrays.stream(callerClasses).anyMatch(IMAGE_PREVIEW_MATCH);
		
		return !wasCalledByImagePreview;
	}
}

package net.azureaaron.mod.mixins;

import java.lang.StackWalker.StackFrame;
import java.util.Arrays;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.sound.MusicSound;

@Mixin(MusicTracker.class)
public class MusicTrackerMixin {
	
	private String[] findCallerMethods(Stream<StackFrame> stackFrameStream) {
		return stackFrameStream.filter(frame -> frame.getMethodName().contains("method"))
				.map(f -> f.getMethodName())
				.toArray(String[]::new);
	}
	
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/sound/MusicSound;shouldReplaceCurrentMusic()Z"))
	private boolean aaronMod$dontReplaceMusicSometimes(MusicSound sound, Operation<Boolean> operation) {
		//Walking the stack is done to ensure that we only return false when Screen#tick isn't the reason for this method invocation
		String[] callerMethods = StackWalker.getInstance().walk(this::findCallerMethods);
		boolean calledScreenTick = Arrays.stream(callerMethods).anyMatch("method_25393"::equals);
		
		return AaronModConfigManager.get().stopSoundsOnWorldChange && !calledScreenTick ? false : operation.call(sound);
	}
}

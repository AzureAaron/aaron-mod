package net.azureaaron.mod.mixins;

import java.lang.StackWalker.StackFrame;
import java.util.Arrays;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.sound.MusicSound;

@Mixin(MusicTracker.class)
public class MusicTrackerMixin {
	@Unique
	private static final String SCREEN_TICK = FabricLoader.getInstance().getMappingResolver().mapMethodName("intermediary", "net.minecraft.class_437", "method_25393", "()V");

	@Unique
	private String[] findCallerMethods(Stream<StackFrame> stackFrameStream) {
		return stackFrameStream.map(f -> f.getMethodName())
				.toArray(String[]::new);
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/sound/MusicSound;shouldReplaceCurrentMusic()Z"))
	private boolean aaronMod$dontReplaceMusicSometimes(MusicSound sound, Operation<Boolean> operation) {
		//Walking the stack is done to ensure that we only return false when Screen#tick isn't the reason for this method invocation
		String[] callerMethods = StackWalker.getInstance().walk(this::findCallerMethods);
		boolean calledScreenTick = Arrays.stream(callerMethods).anyMatch(SCREEN_TICK::equals);

		return AaronModConfigManager.get().stopSoundsOnWorldChange && !calledScreenTick ? false : operation.call(sound);
	}
}

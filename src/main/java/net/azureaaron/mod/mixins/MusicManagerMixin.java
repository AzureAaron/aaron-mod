package net.azureaaron.mod.mixins;

import java.lang.StackWalker.StackFrame;
import java.util.Arrays;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.sounds.MusicManager;

@Mixin(MusicManager.class)
public class MusicManagerMixin {
	@Unique
	private static final String SCREEN_TICK = FabricLoader.getInstance().getMappingResolver().mapMethodName("intermediary", "net.minecraft.class_437", "method_25393", "()V");

	@Unique
	private static String[] findCallerMethods(Stream<StackFrame> stackFrameStream) {
		return stackFrameStream
				.map(StackFrame::getMethodName)
				.toArray(String[]::new);
	}

	@ModifyReturnValue(method = "canReplace", at = @At("RETURN"))
	private static boolean aaronMod$dontReplaceMusicSometimes(boolean original) {
		//Walking the stack is done to ensure that we only return false when Screen#tick isn't the reason for this method invocation
		String[] callerMethods = StackWalker.getInstance().walk(MusicManagerMixin::findCallerMethods);
		boolean calledScreenTick = Arrays.stream(callerMethods).anyMatch(SCREEN_TICK::equals);

		return AaronModConfigManager.get().refinements.music.uninterruptedMusic && !calledScreenTick ? false : original;
	}
}

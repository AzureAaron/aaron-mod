package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.azureaaron.mod.events.PingResultCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PingDebugMonitor;

@Mixin(PingDebugMonitor.class)
public class PingMeasurerMixin {

	@ModifyArg(method = "onPongReceived", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/debugchart/LocalSampleLogger;logSample(J)V"))
	private long aaronMod$onPingResult(long ping) {
		Minecraft.getInstance().schedule(() -> PingResultCallback.EVENT.invoker().onPingResult(ping));

		return ping;
	}
}

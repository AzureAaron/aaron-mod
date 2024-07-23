package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.azureaaron.mod.events.PingResultEvent;
import net.minecraft.client.network.PingMeasurer;

@Mixin(PingMeasurer.class)
public class PingMeasurerMixin {

	@ModifyArg(method = "onPingResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/MultiValueDebugSampleLogImpl;push(J)V"))
	private long aaronMod$onPingResult(long ping) {
		PingResultEvent.EVENT.invoker().onPingResult(ping);

		return ping;
	}
}

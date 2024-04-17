package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.events.PingResultEvent;
import net.minecraft.client.network.PingMeasurer;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;

@Mixin(PingMeasurer.class)
public class PingMeasurerMixin {

	@WrapOperation(method = "onPingResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/MultiValueDebugSampleLogImpl;push(J)V"))
	private void aaronMod$onPingResult(MultiValueDebugSampleLogImpl log, long ping, Operation<Void> operation) {
		PingResultEvent.EVENT.invoker().onPingResult(ping);

		operation.call(log, ping);
	}
}

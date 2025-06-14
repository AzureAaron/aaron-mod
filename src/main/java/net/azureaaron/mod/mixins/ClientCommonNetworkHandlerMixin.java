package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.utils.ServerTickCounter;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;

@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {

	@Inject(method = "onPing", at = @At("RETURN"))
	private void aaronMod$onServerTick(CommonPingS2CPacket packet, CallbackInfo ci) {
		ServerTickCounter.INSTANCE.onServerTick(packet);
	}
}

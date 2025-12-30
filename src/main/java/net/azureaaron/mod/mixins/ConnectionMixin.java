package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.utils.ServerTickCounter;
import net.minecraft.network.Connection;

@Mixin(Connection.class)
public class ConnectionMixin {

	@Inject(method = "genericsFtw", at = @At("HEAD"))
	private static void aaronMod$onReceivePacket(CallbackInfo ci) {
		ServerTickCounter.INSTANCE.onReceivePacket();
	}
}

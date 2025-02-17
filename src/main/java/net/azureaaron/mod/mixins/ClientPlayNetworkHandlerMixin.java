package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.ParticleSpawnEvent;
import net.azureaaron.mod.events.PlaySoundEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {

	protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
		super(client, connection, connectionState);
	}

	@WrapWithCondition(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/MusicTracker;stop()V"))
	private boolean aaronMod$onWorldChange(MusicTracker musicTracker) {
		return !AaronModConfigManager.get().stopSoundsOnWorldChange;
	}

	@Inject(method = "onPlaySound", at = @At("RETURN"))
	private void aaronMod$onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
		PlaySoundEvent.EVENT.invoker().onPlaySound(packet);
	}

	@Inject(method = "onParticle", at = @At("RETURN"))
	private void aaronMod$onParticleSpawn(ParticleS2CPacket packet, CallbackInfo ci) {
		ParticleSpawnEvent.EVENT.invoker().onParticleSpawn(packet);
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowPacketSizeAndPingCharts()Z"))
	private boolean aaronMod$sendPingPackets4PingDisplay(boolean original) {
		return client.getDebugHud().shouldShowDebugHud() ? original : original || AaronModConfigManager.get().pingDisplay;
	}
}

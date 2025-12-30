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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {

	protected ClientPacketListenerMixin(Minecraft client, Connection connection, CommonListenerCookie connectionState) {
		super(client, connection, connectionState);
	}

	@WrapWithCondition(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/MusicManager;stopPlaying()V"))
	private boolean aaronMod$onWorldChange(MusicManager musicTracker) {
		return !AaronModConfigManager.get().refinements.music.uninterruptedMusic;
	}

	@Inject(method = "handleSoundEvent", at = @At("RETURN"))
	private void aaronMod$onPlaySound(ClientboundSoundPacket packet, CallbackInfo ci) {
		PlaySoundEvent.EVENT.invoker().onPlaySound(packet);
	}

	@Inject(method = "handleParticleEvent", at = @At("RETURN"))
	private void aaronMod$onParticleSpawn(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
		ParticleSpawnEvent.EVENT.invoker().onParticleSpawn(packet);
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;showNetworkCharts()Z"))
	private boolean aaronMod$sendPingPackets4PingDisplay(boolean original) {
		return this.minecraft.getDebugOverlay().showDebugScreen() ? original : original || AaronModConfigManager.get().uiAndVisuals.pingHud.enablePingHud;
	}
}

package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.cbyrne.betterinject.annotations.Arg;
import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.Config;
import net.azureaaron.mod.events.PlaySoundEvent;
import net.azureaaron.mod.events.TeamUpdateEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	
	@Redirect(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/MusicTracker;stop()V", ordinal = 0))
	private void aaronMod$onWorldChange(MusicTracker musicTracker) {
		if(!Config.stopSoundsOnWorldChange) musicTracker.stop();
	}
	
	@Inject(method = "onPlaySound", at = @At("HEAD"))
	private void aaronMod$onPlaySound(@Arg PlaySoundS2CPacket packet) {
		PlaySoundEvent.EVENT.invoker().onPlaySound(packet);
	}
	
	@Inject(method = "onTeam", at = @At("HEAD"))
	private void aaronMod$onTeamUpdate(@Arg TeamS2CPacket packet) {
		TeamUpdateEvent.EVENT.invoker().onTeamUpdate(packet);
	}
}

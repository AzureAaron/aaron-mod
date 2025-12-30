package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;

@Mixin(WinScreen.class)
public class WinScreenMixin {

	@WrapWithCondition(method = "removed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/MusicManager;stopPlaying(Lnet/minecraft/sounds/Music;)V"))
	private boolean aaronMod$dontStopCreditsMusic(MusicManager musicTracker, Music type) {
		return !AaronModConfigManager.get().refinements.music.uninterruptedMusic;
	}
}

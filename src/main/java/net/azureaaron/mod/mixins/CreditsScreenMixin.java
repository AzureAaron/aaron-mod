package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.sound.MusicSound;

@Mixin(CreditsScreen.class)
public class CreditsScreenMixin {

	@WrapWithCondition(method = "removed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/MusicTracker;stop(Lnet/minecraft/sound/MusicSound;)V"))
	private boolean aaronMod$dontStopCreditsMusic(MusicTracker musicTracker, MusicSound type) {
		return !AaronModConfigManager.get().refinements.music.uninterruptedMusic;
	}
}

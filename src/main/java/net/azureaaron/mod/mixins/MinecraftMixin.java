package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	private Screen screen;

	@WrapWithCondition(method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;stop()V"))
	private boolean aaronMod$onWorldChange(SoundManager soundManager) {
		return !AaronModConfigManager.get().refinements.music.uninterruptedMusic;
	}

	@Inject(method = "resizeDisplay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;resize(II)V"))
	private void aaronMod$onResolutionChange(CallbackInfo ci) {
		this.screen.markResized(false);
	}
}

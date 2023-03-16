package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.azureaaron.mod.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	
	@Redirect(method = "reset", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;stopAll()V", ordinal = 0))
	private void aaronMod$onWorldChange(SoundManager soundManager) {
		if(!Config.stopSoundsOnWorldChange) soundManager.stopAll();
	}
}

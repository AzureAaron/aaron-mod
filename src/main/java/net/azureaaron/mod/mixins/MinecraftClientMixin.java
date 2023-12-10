package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	
	@WrapWithCondition(method = "reset", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;stopAll()V", ordinal = 0))
	private boolean aaronMod$onWorldChange(SoundManager soundManager) {
		return !AaronModConfigManager.get().stopSoundsOnWorldChange;
	}
}

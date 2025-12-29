package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.SoundManager;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	private Screen currentScreen;

	@WrapWithCondition(method = "setWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;stopAll()V"))
	private boolean aaronMod$onWorldChange(SoundManager soundManager) {
		return !AaronModConfigManager.get().refinements.music.uninterruptedMusic;
	}

	@Inject(method = "onResolutionChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;resize(II)V"))
	private void aaronMod$onResolutionChange(CallbackInfo ci) {
		this.currentScreen.markResized(false);
	}
}

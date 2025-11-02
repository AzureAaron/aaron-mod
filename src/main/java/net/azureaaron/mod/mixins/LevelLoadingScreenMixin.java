package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin {

	@Inject(method = { "render", "renderBackground" }, at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideWorldLoadingScreen(CallbackInfo ci) {
		if (AaronModConfigManager.get().uiAndVisuals.world.hideWorldLoadingScreen) ci.cancel();
	}
}

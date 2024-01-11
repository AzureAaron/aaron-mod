package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
	
	@Inject(method = "render", at = @At("HEAD"))
	private void aaronMod$fixTranslucency(CallbackInfo ci, @Local(argsOnly = true) DrawContext context) {
		if (AaronModConfigManager.get().fixTabTranslucency) {
			context.getMatrices().translate(0f, 0f, 200f);
		}
	}
}

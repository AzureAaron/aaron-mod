package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.cbyrne.betterinject.annotations.Arg;
import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
	
	@Inject(method = "render", at = @At("HEAD"))
	private void aaronMod$fixTranslucency(@Arg DrawContext context) {
		if(Config.fixTabTranslucency) {
			context.getMatrices().translate(0f, 0f, 200f);
		}
	}
}

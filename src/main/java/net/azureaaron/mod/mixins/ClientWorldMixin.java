package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

	@ModifyExpressionValue(method = "getLightningTicksLeft", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z", remap = false))
	private boolean aaronMod$hideLightningFlashes(boolean shouldHideLightningFlashes) {
		return shouldHideLightningFlashes || AaronModConfigManager.get().uiAndVisuals.world.hideLightning;
	}
}

package net.azureaaron.mod.mixins;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.entity.Entity;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

	@Shadow
	@Nullable
	private static BackgroundRenderer.StatusEffectFogModifier getFogModifier(Entity entity, float tickDelta) {
		return null;
	}

	@Inject(method = "applyFog", at = @At("RETURN"))
	private static void aaronMod$noFog(CallbackInfo ci, @Local(argsOnly = true) BackgroundRenderer.FogType fogType, @Local(argsOnly = true) boolean thickFog, @Local(argsOnly = true, ordinal = 1) float tickDelta, @Local CameraSubmersionType cameraSubmersionType, @Local Entity entity) {
		//For some reason @Local can't capture this variable in the target method .-.
		BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);

		if (AaronModConfigManager.get().noFog && statusEffectFogModifier == null && cameraSubmersionType == CameraSubmersionType.NONE && (fogType == BackgroundRenderer.FogType.FOG_TERRAIN || thickFog)) {
			RenderSystem.setShaderFogColor(1f, 1f, 1f, 0f);
		}
	}
}

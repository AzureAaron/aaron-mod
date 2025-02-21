package net.azureaaron.mod.mixins;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Fog;
import net.minecraft.entity.Entity;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

	@Shadow
	@Nullable
	private static BackgroundRenderer.StatusEffectFogModifier getFogModifier(Entity entity, float tickDelta) {
		return null;
	}

	@ModifyReturnValue(method = "applyFog", at = @At("RETURN"))
	private static Fog aaronMod$hideTerrainFog(Fog original, @Local(argsOnly = true) BackgroundRenderer.FogType fogType, @Local(argsOnly = true) boolean thickFog, @Local(argsOnly = true, ordinal = 1) float tickDelta, @Local CameraSubmersionType cameraSubmersionType, @Local Entity entity) {
		//For some reason @Local can't capture this variable in the target method .-.
		BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);

		if (AaronModConfigManager.get().uiAndVisuals.world.hideFog && statusEffectFogModifier == null && cameraSubmersionType == CameraSubmersionType.NONE && (fogType == BackgroundRenderer.FogType.FOG_TERRAIN || thickFog)) {
			Fog newFog = new Fog(original.start(), original.end(), original.shape(), 1f, 1f, 1f, 0f);

			return newFog;
		}

		return original;
	}
}

package net.azureaaron.mod.mixins;

import org.joml.Vector4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.client.renderer.fog.environment.MobEffectFogEnvironment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

	@WrapOperation(method = "setupFog(Lnet/minecraft/client/Camera;ILnet/minecraft/client/DeltaTracker;FLnet/minecraft/client/multiplayer/ClientLevel;)Lorg/joml/Vector4f;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;isApplicable(Lnet/minecraft/world/level/material/FogType;Lnet/minecraft/world/entity/Entity;)Z"))
	private boolean aaronMod$checkIfEffectModifierApplies(FogEnvironment modifier, FogType submersionType, Entity cameraEntity, Operation<Boolean> operation, @Share("effectModifierApplied") LocalBooleanRef effectModifierApplied) {
		boolean applies = operation.call(modifier, submersionType, cameraEntity);

		if (modifier instanceof MobEffectFogEnvironment) {
			effectModifierApplied.set(effectModifierApplied.get() | applies);
		}

		return applies;
	}

	@Inject(method = "setupFog(Lnet/minecraft/client/Camera;ILnet/minecraft/client/DeltaTracker;FLnet/minecraft/client/multiplayer/ClientLevel;)Lorg/joml/Vector4f;", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/fog/FogData;renderDistanceEnd:F", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
	private void aaronMod$noTerrainFog(CallbackInfoReturnable<Vector4f> cir, @Local FogType submersionType, @Local FogData fogData, @Share("effectModifierApplied") LocalBooleanRef effectModifierApplied) {
		if (AaronModConfigManager.get().uiAndVisuals.world.hideFog && !effectModifierApplied.get() && (submersionType == FogType.ATMOSPHERIC)) {
			fogData.environmentalStart = Float.MAX_VALUE;
			fogData.environmentalEnd = Float.MAX_VALUE;
			fogData.renderDistanceStart = Float.MAX_VALUE;
			fogData.renderDistanceEnd = Float.MAX_VALUE;
		}
	}
}

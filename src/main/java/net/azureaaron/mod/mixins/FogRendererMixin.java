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
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.render.fog.StatusEffectFogModifier;
import net.minecraft.entity.Entity;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

	@WrapOperation(method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/fog/FogModifier;shouldApply(Lnet/minecraft/block/enums/CameraSubmersionType;Lnet/minecraft/entity/Entity;)Z"))
	private boolean aaronMod$checkIfEffectModifierApplies(FogModifier modifier, CameraSubmersionType submersionType, Entity cameraEntity, Operation<Boolean> operation, @Share("effectModifierApplied") LocalBooleanRef effectModifierApplied) {
		boolean applies = operation.call(modifier, submersionType, cameraEntity);

		if (modifier instanceof StatusEffectFogModifier) {
			effectModifierApplied.set(effectModifierApplied.get() | applies);
		}

		return applies;
	}

	@Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/fog/FogData;renderDistanceEnd:F", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
	private void aaronMod$noTerrainFog(CallbackInfoReturnable<Vector4f> cir, @Local(argsOnly = true) boolean thick, @Local CameraSubmersionType submersionType, @Local FogData fogData, @Share("effectModifierApplied") LocalBooleanRef effectModifierApplied) {
		if (AaronModConfigManager.get().uiAndVisuals.world.hideFog && !effectModifierApplied.get() && (submersionType == CameraSubmersionType.ATMOSPHERIC || thick)) {
			fogData.environmentalStart = Float.MAX_VALUE;
			fogData.environmentalEnd = Float.MAX_VALUE;
			fogData.renderDistanceStart = Float.MAX_VALUE;
			fogData.renderDistanceEnd = Float.MAX_VALUE;
		}
	}
}

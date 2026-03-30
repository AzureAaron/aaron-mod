package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.skyblock.entity.MobGlow;
import net.azureaaron.mod.utils.render.GlowRenderer;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ModelPartFeatureRenderer;

@Mixin(ModelPartFeatureRenderer.class)
public class ModelPartFeatureRendererMixin {

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeStorage$ModelPartSubmit;outlineColor()I"), require = 2)
	private int aaronMod$useCustomGlowColour(SubmitNodeStorage.ModelPartSubmit submit, Operation<Integer> operation) {
		return submit.aaronMod$getCustomGlowColour() != MobGlow.NO_GLOW ? submit.aaronMod$getCustomGlowColour() : operation.call(submit);
	}

	@ModifyVariable(method = "render", at = @At("LOAD"), name = "outlineBufferSource", require = 2)
	private OutlineBufferSource aaronMod$useCustomGlowConsumers(OutlineBufferSource original, @Local(name = "modelPartSubmit") SubmitNodeStorage.ModelPartSubmit submit) {
		return submit.aaronMod$getCustomGlowColour() != MobGlow.NO_GLOW ? GlowRenderer.getInstance().getGlowVertexConsumers() : original;
	}
}

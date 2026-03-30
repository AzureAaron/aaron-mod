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
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;

@Mixin(ItemFeatureRenderer.class)
public class ItemFeatureRendererMixin {

	@WrapOperation(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeStorage$ItemSubmit;outlineColor()I"), require = 3)
	private int aaronMod$useCustomGlowColour(SubmitNodeStorage.ItemSubmit submit, Operation<Integer> operation) {
		return submit.aaronMod$getCustomGlowColour() != MobGlow.NO_GLOW ? submit.aaronMod$getCustomGlowColour() : operation.call(submit);
	}

	@ModifyVariable(method = "renderItem", at = @At("LOAD"), name = "outlineBufferSource", require = 2)
	private OutlineBufferSource aaronMod$useCustomGlowConsumers(OutlineBufferSource original, @Local(name = "submit") SubmitNodeStorage.ItemSubmit submit) {
		return submit.aaronMod$getCustomGlowColour() != MobGlow.NO_GLOW ? GlowRenderer.getInstance().getGlowVertexConsumers() : original;
	}
}

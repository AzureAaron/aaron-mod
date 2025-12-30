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
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;

@Mixin(ModelFeatureRenderer.class)
public class ModelCommandRendererMixin {

	@WrapOperation(method = "renderModel(Lnet/minecraft/client/renderer/SubmitNodeStorage$ModelSubmit;Lnet/minecraft/client/renderer/rendertype/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeStorage$ModelSubmit;outlineColor()I"), require = 2)
	private <S> int aaronMod$useCustomGlowColour(SubmitNodeStorage.ModelSubmit<S> command, Operation<Integer> operation) {
		return command.aaronMod$getCustomGlowColour() != MobGlow.NO_GLOW ? command.aaronMod$getCustomGlowColour() : operation.call(command);
	}

	@ModifyVariable(method = "renderModel(Lnet/minecraft/client/renderer/SubmitNodeStorage$ModelSubmit;Lnet/minecraft/client/renderer/rendertype/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V", at = @At("LOAD"), argsOnly = true, require = 2)
	private <S> OutlineBufferSource aaronMod$useCustomGlowConsumers(OutlineBufferSource original, @Local(argsOnly = true) SubmitNodeStorage.ModelSubmit<S> command) {
		return command.aaronMod$getCustomGlowColour() != MobGlow.NO_GLOW ? GlowRenderer.getInstance().getGlowVertexConsumers() : original;
	}
}

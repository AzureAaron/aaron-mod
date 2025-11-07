package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.skyblock.entity.MobGlow;
import net.azureaaron.mod.utils.render.GlowRenderer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;

@Mixin(ModelCommandRenderer.class)
public class ModelCommandRendererMixin {

	@WrapOperation(method = "render(Lnet/minecraft/client/render/command/OrderedRenderCommandQueueImpl$ModelCommand;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/OutlineVertexConsumerProvider;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueueImpl$ModelCommand;outlineColor()I"), require = 2)
	private <S> int aaronMod$useCustomGlowColour(OrderedRenderCommandQueueImpl.ModelCommand<S> command, Operation<Integer> operation) {
		return command.aaronMod$getCustomGlowColour() != MobGlow.NO_GLOW ? command.aaronMod$getCustomGlowColour() : operation.call(command);
	}

	@ModifyVariable(method = "render(Lnet/minecraft/client/render/command/OrderedRenderCommandQueueImpl$ModelCommand;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/OutlineVertexConsumerProvider;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V", at = @At("LOAD"), argsOnly = true, require = 2)
	private <S> OutlineVertexConsumerProvider aaronMod$useCustomGlowConsumers(OutlineVertexConsumerProvider original, @Local(argsOnly = true) OrderedRenderCommandQueueImpl.ModelCommand<S> command) {
		return command.aaronMod$getCustomGlowColour() != MobGlow.NO_GLOW ? GlowRenderer.getInstance().getGlowVertexConsumers() : original;
	}
}

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
import net.minecraft.client.render.command.ModelPartCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;

@Mixin(ModelPartCommandRenderer.class)
public class ModelPartCommandRendererMixin {

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueueImpl$ModelPartCommand;outlineColor()I"), require = 2)
	private int aaronMod$useCustomGlowColour(OrderedRenderCommandQueueImpl.ModelPartCommand command, Operation<Integer> operation) {
		return command.aaronMod$getCustomGlowColour() != MobGlow.NO_GLOW ? command.aaronMod$getCustomGlowColour() : operation.call(command);
	}

	@ModifyVariable(method = "render", at = @At("LOAD"), argsOnly = true, require = 2)
	private OutlineVertexConsumerProvider aaronMod$useCustomGlowConsumers(OutlineVertexConsumerProvider original, @Local OrderedRenderCommandQueueImpl.ModelPartCommand command) {
		return command.aaronMod$getCustomGlowColour() != MobGlow.NO_GLOW ? GlowRenderer.getInstance().getGlowVertexConsumers() : original;
	}
}

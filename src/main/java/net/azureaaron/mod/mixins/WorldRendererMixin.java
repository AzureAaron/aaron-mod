package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.render.GlowRenderer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@ModifyExpressionValue(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilderStorage;getOutlineVertexConsumers()Lnet/minecraft/client/render/OutlineVertexConsumerProvider;"))
	private OutlineVertexConsumerProvider aaronMod$useCustomGlowOutlineVertexConsumers(OutlineVertexConsumerProvider original, @Local Entity entity) {
		return entity instanceof EnderDragonEntity && Cache.inM7Phase5 && AaronModConfigManager.get().skyblock.m7.glowingDragons ? GlowRenderer.GLOW_OUTLINE_VERTEX_CONSUMERS : original;
	}

	@Inject(method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V"))
	private void aaronMod$drawGlowVertexConsumers(CallbackInfo ci) {
		GlowRenderer.GLOW_OUTLINE_VERTEX_CONSUMERS.draw();
	}
}

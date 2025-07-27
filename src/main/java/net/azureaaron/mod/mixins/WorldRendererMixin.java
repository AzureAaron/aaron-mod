package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
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
	@Unique
	private boolean hasGlowThisFrame = false;

	@ModifyArg(method = "getEntitiesToRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"))
	private Entity aaronMod$markIfGlowRendering(Entity entity) {
		this.hasGlowThisFrame |= shouldEntityGlow(entity);
		return entity;
	}

	@Inject(method = "method_62214",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;canDrawEntityOutlines()Z")),
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearColorAndDepthTextures(Lcom/mojang/blaze3d/textures/GpuTexture;ILcom/mojang/blaze3d/textures/GpuTexture;D)V", ordinal = 0, shift = At.Shift.AFTER)
	)
	private void aaronMod$updateGlowDepthTexDepth(CallbackInfo ci) {
		if (this.hasGlowThisFrame) {
			GlowRenderer.getInstance().updateGlowDepthTexDepth();
			//We can set this to false now since we just use this to know if we should update the depth texture or not
			this.hasGlowThisFrame = false;
		}
	}

	@ModifyExpressionValue(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilderStorage;getOutlineVertexConsumers()Lnet/minecraft/client/render/OutlineVertexConsumerProvider;"))
	private OutlineVertexConsumerProvider aaronMod$useCustomGlowOutlineVertexConsumers(OutlineVertexConsumerProvider original, @Local Entity entity) {
		return shouldEntityGlow(entity) ? GlowRenderer.getInstance().getGlowVertexConsumers() : original;
	}

	@Inject(method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V"))
	private void aaronMod$drawGlowVertexConsumers(CallbackInfo ci) {
		GlowRenderer.getInstance().getGlowVertexConsumers().draw();
	}

	@Unique
	private boolean shouldEntityGlow(Entity entity) {
		return entity instanceof EnderDragonEntity && Cache.inM7Phase5 && AaronModConfigManager.get().skyblock.m7.glowingDragons;
	}
}

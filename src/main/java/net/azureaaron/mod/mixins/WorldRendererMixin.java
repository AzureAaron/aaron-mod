package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.mixins.accessors.FogRendererAccessor;
import net.azureaaron.mod.mixins.accessors.GameRendererAccessor;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.render.GlowRenderer;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.render.fog.FogRenderer.FogType;
import net.minecraft.client.render.fog.StatusEffectFogModifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@ModifyExpressionValue(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilderStorage;getOutlineVertexConsumers()Lnet/minecraft/client/render/OutlineVertexConsumerProvider;"))
	private OutlineVertexConsumerProvider aaronMod$useCustomGlowOutlineVertexConsumers(OutlineVertexConsumerProvider original, @Local Entity entity) {
		return entity instanceof EnderDragonEntity && Cache.inM7Phase5 && AaronModConfigManager.get().skyblock.m7.glowingDragons ? GlowRenderer.GLOW_OUTLINE_VERTEX_CONSUMERS : original;
	}

	@Inject(method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V"))
	private void aaronMod$drawGlowVertexConsumers(CallbackInfo ci) {
		GlowRenderer.GLOW_OUTLINE_VERTEX_CONSUMERS.draw();
	}

	@ModifyVariable(method = "render",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderSky(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Camera;FLcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", shift = At.Shift.AFTER)),
			at = @At(value = "LOAD"), argsOnly = true)
	private GpuBufferSlice aaronMod$noTerrainFog(GpuBufferSlice worldFogBuffer, @Local(argsOnly = true) Camera camera, @Local(argsOnly = true, ordinal = 1) boolean shouldRenderSky) {
		if (AaronModConfigManager.get().uiAndVisuals.world.hideFog) {
			FogRenderer fogRenderer = ((GameRendererAccessor) this.client.gameRenderer).getFogRenderer();
			boolean thickFog = !shouldRenderSky; //This parameters value is the inversion of the thickFog boolean in GameRenderer#renderWorld
			CameraSubmersionType cameraSubmersionType = ((FogRendererAccessor) fogRenderer).invokeGetCameraSubmersionType(camera, thickFog);
			boolean hasStatusEffectModifier = false;

			for (FogModifier modifier : FogRendererAccessor.getFOG_MODIFIERS()) {
				if (modifier instanceof StatusEffectFogModifier) {
					hasStatusEffectModifier |= modifier.shouldApply(cameraSubmersionType, camera.getFocusedEntity());
				}
			}

			if (!hasStatusEffectModifier && cameraSubmersionType == CameraSubmersionType.ATMOSPHERIC) {
				return fogRenderer.getFogBuffer(FogType.NONE);
			}
		}

		return worldFogBuffer;
	}
}

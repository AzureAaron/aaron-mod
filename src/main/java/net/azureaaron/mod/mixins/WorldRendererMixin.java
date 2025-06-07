package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.buffers.GpuBufferSlice;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.mixins.accessors.FogRendererAccessor;
import net.azureaaron.mod.mixins.accessors.GameRendererAccessor;
import net.azureaaron.mod.utils.Cache;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.render.fog.FogRenderer.FogType;
import net.minecraft.client.render.fog.StatusEffectFogModifier;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	@Final
	private MinecraftClient client;
	@Shadow
	@Final
	private DefaultFramebufferSet framebufferSet;

	@Inject(method = "method_62214",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;canDrawEntityOutlines()Z")),
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearColorAndDepthTextures(Lcom/mojang/blaze3d/textures/GpuTexture;ILcom/mojang/blaze3d/textures/GpuTexture;D)V", ordinal = 0, shift = At.Shift.AFTER)
	)
	private void aaronMod$copyFramebufferDepth2AdjustGlowVisibility(CallbackInfo ci, @Share(namespace = "c", value = "copiedOutlineDepth") LocalBooleanRef copiedOutlineDepth) {
		if (Cache.inM7Phase5 && AaronModConfigManager.get().skyblock.m7.glowingDragons && !copiedOutlineDepth.get()) {
			framebufferSet.entityOutlineFramebuffer.get().copyDepthFrom(framebufferSet.mainFramebuffer.get());
			copiedOutlineDepth.set(true);
		}
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

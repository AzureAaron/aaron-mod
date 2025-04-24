package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Cache;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
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
}

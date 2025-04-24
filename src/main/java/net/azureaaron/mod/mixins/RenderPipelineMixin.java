package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Cache;
import net.minecraft.client.gl.RenderPipelines;

@Mixin(RenderPipeline.class)
public class RenderPipelineMixin {

	@ModifyReturnValue(method = "getDepthTestFunction", at = @At("RETURN"))
	private DepthTestFunction aaronMod$modifyGlowDepthTest(DepthTestFunction original) {
		return ((Object) this == RenderPipelines.OUTLINE_CULL || (Object) this == RenderPipelines.OUTLINE_NO_CULL) && Cache.inM7Phase5 && AaronModConfigManager.get().skyblock.m7.glowingDragons ? DepthTestFunction.LEQUAL_DEPTH_TEST : original;
	}
}

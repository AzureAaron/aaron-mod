package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.azureaaron.mod.utils.render.AaronModRenderPipelines;
import net.azureaaron.mod.utils.render.GlowRenderer;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;

@Mixin(PreparedRenderType.class)
public abstract class PreparedRenderTypeMixin {
	@Shadow
	public abstract RenderPipeline pipeline();

	@ModifyExpressionValue(method = "drawFromBuffer(Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/IndexType;III)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;getDepthTextureView()Lcom/mojang/blaze3d/textures/GpuTextureView;"))
	private GpuTextureView aaronMod$useGlowDepthTex(GpuTextureView original) {
		if (this.pipeline() == AaronModRenderPipelines.OUTLINE_DEPTH_CULL || this.pipeline() == AaronModRenderPipelines.OUTLINE_DEPTH_NO_CULL) {
			return GlowRenderer.INSTANCE.getGlowDepthTexture();
		}

		return original;
	}
}

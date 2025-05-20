package net.azureaaron.mod.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;

@Mixin(RenderPipelines.class)
public class RenderPipelinesMixin {

	@ModifyReceiver(method = "<clinit>", at = {
			@At(value = "net.azureaaron.mod.utils.render.RenderPipelineInjectionPoint", target = "Lnet/minecraft/client/gl/RenderPipelines;RENDERTYPE_TEXT:Lcom/mojang/blaze3d/pipeline/RenderPipeline;", opcode = Opcodes.PUTSTATIC),
			@At(value = "net.azureaaron.mod.utils.render.RenderPipelineInjectionPoint", target = "Lnet/minecraft/client/gl/RenderPipelines;RENDERTYPE_TEXT_POLYGON_OFFSET:Lcom/mojang/blaze3d/pipeline/RenderPipeline;", opcode = Opcodes.PUTSTATIC)
			}, require = 2)
	private static RenderPipeline.Builder aaronMod$injectChromaShaderProperties(RenderPipeline.Builder builder) {
		return builder.withUniform("ScreenSize", UniformType.VEC2)
				.withUniform("AaronTicks", UniformType.FLOAT)
				.withUniform("AaronChromaSize", UniformType.FLOAT)
				.withUniform("AaronChromaSaturation", UniformType.FLOAT)
				.withUniform("AaronChromaSpeed", UniformType.FLOAT)
				.withShaderDefine("AARON_MOD_CHROMA");
	}
}

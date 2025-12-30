package net.azureaaron.mod.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import net.minecraft.client.renderer.RenderPipelines;

@Mixin(RenderPipelines.class)
public class RenderPipelinesMixin {

	@ModifyReceiver(method = "<clinit>", at = {
			@At(value = "net.azureaaron.mod.utils.render.RenderPipelineInjectionPoint", target = "Lnet/minecraft/client/renderer/RenderPipelines;TEXT:Lcom/mojang/blaze3d/pipeline/RenderPipeline;", opcode = Opcodes.PUTSTATIC),
			@At(value = "net.azureaaron.mod.utils.render.RenderPipelineInjectionPoint", target = "Lnet/minecraft/client/renderer/RenderPipelines;TEXT_POLYGON_OFFSET:Lcom/mojang/blaze3d/pipeline/RenderPipeline;", opcode = Opcodes.PUTSTATIC),
			@At(value = "net.azureaaron.mod.utils.render.RenderPipelineInjectionPoint", target = "Lnet/minecraft/client/renderer/RenderPipelines;GUI_TEXT:Lcom/mojang/blaze3d/pipeline/RenderPipeline;", opcode = Opcodes.PUTSTATIC)
			}, require = 3)
	private static RenderPipeline.Builder aaronMod$injectChromaShaderProperties(RenderPipeline.Builder builder) {
		return builder.withUniform("Globals", UniformType.UNIFORM_BUFFER)
				.withUniform("Chroma", UniformType.UNIFORM_BUFFER)
				.withShaderDefine("AARON_MOD_CHROMA");
	}
}

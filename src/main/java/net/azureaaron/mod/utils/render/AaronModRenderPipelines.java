package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;

public class AaronModRenderPipelines {
	public static final RenderPipeline CHROMA_GUI = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation(Identifier.of(Main.NAMESPACE, "pipeline/chroma_gui"))
			.withVertexShader(Identifier.of(Main.NAMESPACE, "core/chroma_gui"))
			.withFragmentShader(Identifier.of(Main.NAMESPACE, "core/chroma_gui"))
			.withUniform("Globals", UniformType.UNIFORM_BUFFER)
			.withUniform("Chroma", UniformType.UNIFORM_BUFFER)
			.build());

	@Init
	public static void init() {}
}

package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class AaronModRenderPipelines {
	public static final RenderPipeline CHROMA_GUI = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation(Identifier.fromNamespaceAndPath(Main.NAMESPACE, "pipeline/chroma_gui"))
			.withVertexShader(Identifier.fromNamespaceAndPath(Main.NAMESPACE, "core/chroma_gui"))
			.withFragmentShader(Identifier.fromNamespaceAndPath(Main.NAMESPACE, "core/chroma_gui"))
			.withUniform("Globals", UniformType.UNIFORM_BUFFER)
			.withUniform("Chroma", UniformType.UNIFORM_BUFFER)
			.build());
	public static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Identifier.fromNamespaceAndPath(Main.NAMESPACE, "pipeline/debug_filled_box_through_walls"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build());
	public static final RenderPipeline LINES_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Identifier.fromNamespaceAndPath(Main.NAMESPACE, "pipeline/lines_through_walls"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build());

	@Init
	public static void init() {}
}

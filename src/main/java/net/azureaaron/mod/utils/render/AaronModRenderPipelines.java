package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.minecraft.client.renderer.RenderPipelines;

public class AaronModRenderPipelines {
	public static final RenderPipeline CHROMA_GUI = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_gui"))
			.withVertexShader(Main.id("core/chroma_gui"))
			.withFragmentShader(Main.id("core/chroma_gui"))
			.withUniform("Globals", UniformType.UNIFORM_BUFFER)
			.withUniform("Chroma", UniformType.UNIFORM_BUFFER)
			.build());
	public static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Main.id("pipeline/debug_filled_box_through_walls"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build());
	public static final RenderPipeline LINES_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Main.id("pipeline/lines_through_walls"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build());

	@Init
	public static void init() {}
}

package net.azureaaron.mod.utils.render;

import java.util.Optional;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;

public class AaronModRenderPipelines {
	private static final RenderPipeline.Snippet CHROMA_TEXT_SNIPPET = RenderPipeline.builder()
			.withVertexShader(Main.id("core/chroma_text"))
			.withFragmentShader(Main.id("core/chroma_text"))
			.withShaderDefine("AARON_MOD_CHROMA")
			.withBindGroupLayout(AaronModBindGroupLayouts.CHROMA)
			.buildSnippet();

	public static final RenderPipeline CHROMA_TEXT = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.WORLD_TEXT_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text"))
			.build());
	public static final RenderPipeline CHROMA_GUI_TEXT = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_TEXT_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_gui_text"))
			.build());
	public static final RenderPipeline CHROMA_TEXT_GREYSCALE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.WORLD_TEXT_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text_greyscale"))
			.withShaderDefine("IS_GRAYSCALE")
			.build());
	public static final RenderPipeline CHROMA_GUI_TEXT_GREYSCALE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_TEXT_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_gui_text_greyscale"))
			.withShaderDefine("IS_GRAYSCALE")
			.build());
	public static final RenderPipeline CHROMA_TEXT_POLYGON_OFFSET = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.WORLD_TEXT_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text_polygon_offset"))
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true, 1.0F, 10.0F))
			.build());
	public static final RenderPipeline CHROMA_TEXT_GREYSCALE_POLYGON_OFFSET = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.WORLD_TEXT_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text_greyscale_polygon_offset"))
			.withShaderDefine("IS_GRAYSCALE")
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true, 1.0F, 10.0F))
			.build());
	public static final RenderPipeline CHROMA_TEXT_SEE_THROUGH = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.WORLD_TEXT_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text_see_through"))
			.withShaderDefine("IS_SEE_THROUGH")
			.withDepthStencilState(Optional.empty())
			.build());
	public static final RenderPipeline CHROMA_TEXT_GREYSCALE_SEE_THROUGH = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.WORLD_TEXT_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text_greyscale_see_through"))
			.withShaderDefine("IS_GRAYSCALE")
			.withShaderDefine("IS_SEE_THROUGH")
			.withDepthStencilState(Optional.empty())
			.build());
	public static final RenderPipeline CHROMA_GUI = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_gui"))
			.withVertexShader(Main.id("core/chroma_gui"))
			.withFragmentShader(Main.id("core/chroma_gui"))
			.withBindGroupLayout(AaronModBindGroupLayouts.CHROMA)
			.build());
	public static final RenderPipeline FILLED_INSTANCED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Main.id("pipeline/debug_filled_box_instanced"))
			.withVertexShader(Main.id("core/filled_box"))
			.withBindGroupLayout(AaronModBindGroupLayouts.BOX_DATA)
			.withVertexBinding(0, DefaultVertexFormat.POSITION)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withCull(false)
			.build());
	public static final RenderPipeline FILLED_THROUGH_WALLS_INSTANCED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Main.id("pipeline/debug_filled_box_through_walls_instanced"))
			.withVertexShader(Main.id("core/filled_box"))
			.withBindGroupLayout(AaronModBindGroupLayouts.BOX_DATA)
			.withVertexBinding(0, DefaultVertexFormat.POSITION)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withDepthStencilState(Optional.empty())
			.build());
	public static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Main.id("pipeline/debug_filled_box_through_walls"))
			.withDepthStencilState(Optional.empty())
			.build());
	public static final RenderPipeline OUTLINED_BOX_INSTANCED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Main.id("pipeline/outlined_box_instanced"))
			.withVertexShader(Main.id("core/outlined_box"))
			.withBindGroupLayout(AaronModBindGroupLayouts.OUTLINED_BOX_DATA)
			.withVertexBinding(0, AaronModVertexFormats.POSITION_NORMAL)
			.withPrimitiveTopology(PrimitiveTopology.LINES)
			.build());
	public static final RenderPipeline OUTLINED_BOX_THROUGH_WALLS_INSTANCED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Main.id("pipeline/outlined_box_through_walls_instanced"))
			.withVertexShader(Main.id("core/outlined_box"))
			.withBindGroupLayout(AaronModBindGroupLayouts.OUTLINED_BOX_DATA)
			.withVertexBinding(0, AaronModVertexFormats.POSITION_NORMAL)
			.withPrimitiveTopology(PrimitiveTopology.LINES)
			.withDepthStencilState(Optional.empty())
			.build());
	public static final RenderPipeline LINES_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Main.id("pipeline/lines_through_walls"))
			.withDepthStencilState(Optional.empty())
			.build());
	public static final RenderPipeline BLURRED_RECTANGLE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation(Main.id("pipeline/blurred_rectangle"))
			.withVertexShader("core/position_color")
			.withFragmentShader(Main.id("core/box_blur"))
			.withBindGroupLayout(BindGroupLayouts.SAMPLER0)
			.withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.build());
	public static final RenderPipeline OUTLINE_DEPTH_CULL = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.OUTLINE_SNIPPET)
			.withLocation(Main.id("outline_depth_cull"))
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
			.build());
	public static final RenderPipeline OUTLINE_DEPTH_NO_CULL = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.OUTLINE_SNIPPET)
			.withLocation(Main.id("outline_depth_no_cull"))
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
			.withCull(false)
			.build());

	@Init
	public static void init() {}
}

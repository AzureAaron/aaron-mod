package net.azureaaron.mod.utils.render;

import java.util.Optional;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.minecraft.client.renderer.RenderPipelines;

public class AaronModRenderPipelines {
	private static final RenderPipeline.Snippet CHROMA_TEXT_SNIPPET = RenderPipeline.builder()
			.withShaderDefine("AARON_MOD_CHROMA")
			.withUniform("Chroma", UniformType.UNIFORM_BUFFER)
			.buildSnippet();

	public static final RenderPipeline CHROMA_TEXT = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text"))
			.withVertexShader("core/rendertype_text")
			.withFragmentShader(Main.id("core/chroma_text"))
			.withSampler("Sampler0")
			.withSampler("Sampler2")
			.build());
	public static final RenderPipeline CHROMA_GUI_TEXT = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_gui_text"))
			.withVertexShader("core/rendertype_text")
			.withFragmentShader(Main.id("core/chroma_text"))
			.withSampler("Sampler0")
			.withSampler("Sampler2")
			.build());
	public static final RenderPipeline CHROMA_TEXT_GREYSCALE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text_greyscale"))
			.withVertexShader("core/rendertype_text_intensity")
			.withFragmentShader(Main.id("core/chroma_text_greyscale"))
			.withSampler("Sampler0")
			.withSampler("Sampler2")
			.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true, -1.0F, -10.0F))
			.build());
	public static final RenderPipeline CHROMA_GUI_TEXT_GREYSCALE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_gui_text_greyscale"))
			.withVertexShader("core/rendertype_text_intensity")
			.withFragmentShader(Main.id("core/chroma_text_greyscale"))
			.withSampler("Sampler0")
			.withSampler("Sampler2")
			.build());
	public static final RenderPipeline CHROMA_TEXT_POLYGON_OFFSET = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text_polygon_offset"))
			.withVertexShader("core/rendertype_text")
			.withFragmentShader(Main.id("core/chroma_text"))
			.withSampler("Sampler0")
			.withSampler("Sampler2")
			.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true, -1.0F, -10.0F))
			.build());
	public static final RenderPipeline CHROMA_TEXT_GREYSCALE_POLYGON_OFFSET = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text_greyscale_polygon_offset"))
			.withVertexShader("core/rendertype_text_intensity")
			.withFragmentShader(Main.id("core/chroma_text_greyscale"))
			.withSampler("Sampler0")
			.withSampler("Sampler2")
			.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true, -1.0F, -10.0F))
			.build());
	public static final RenderPipeline CHROMA_TEXT_SEE_THROUGH = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text_see_through"))
			.withVertexShader("core/rendertype_text_see_through")
			.withFragmentShader(Main.id("core/chroma_text_see_through"))
			.withSampler("Sampler0")
			.withDepthStencilState(Optional.empty())
			.build());
	public static final RenderPipeline CHROMA_TEXT_GREYSCALE_SEE_THROUGH = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, CHROMA_TEXT_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_text_greyscale_see_through"))
			.withVertexShader("core/rendertype_text_intensity_see_through")
			.withFragmentShader(Main.id("core/chroma_text_greyscale_see_through"))
			.withSampler("Sampler0")
			.withDepthStencilState(Optional.empty())
			.build());

	public static final RenderPipeline CHROMA_GUI = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
			.withLocation(Main.id("pipeline/chroma_gui"))
			.withVertexShader(Main.id("core/chroma_gui"))
			.withFragmentShader(Main.id("core/chroma_gui"))
			.withUniform("Chroma", UniformType.UNIFORM_BUFFER)
			.build());
	public static final RenderPipeline FILLED_INSTANCED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Main.id("pipeline/debug_filled_box_instanced"))
			.withVertexShader(Main.id("core/filled_box"))
			.withUniform("BoxData", UniformType.TEXEL_BUFFER, TextureFormat.AARON_MOD$RGBA32F)
			.withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
			.withCull(false)
			.build());
	public static final RenderPipeline FILLED_THROUGH_WALLS_INSTANCED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Main.id("pipeline/debug_filled_box_through_walls_instanced"))
			.withVertexShader(Main.id("core/filled_box"))
			.withUniform("BoxData", UniformType.TEXEL_BUFFER, TextureFormat.AARON_MOD$RGBA32F)
			.withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
			.withDepthStencilState(Optional.empty())
			.build());
	public static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Main.id("pipeline/debug_filled_box_through_walls"))
			.withDepthStencilState(Optional.empty())
			.build());
	public static final RenderPipeline OUTLINED_BOX_INSTANCED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Main.id("pipeline/outlined_box_instanced"))
			.withVertexShader(Main.id("core/outlined_box"))
			.withUniform("OutlinedBoxData", UniformType.TEXEL_BUFFER, TextureFormat.AARON_MOD$RGBA32F)
			.withVertexFormat(AaronModVertexFormats.POSITION_NORMAL, VertexFormat.Mode.LINES)
			.build());
	public static final RenderPipeline OUTLINED_BOX_THROUGH_WALLS_INSTANCED = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Main.id("pipeline/outlined_box_through_walls_instanced"))
			.withVertexShader(Main.id("core/outlined_box"))
			.withUniform("OutlinedBoxData", UniformType.TEXEL_BUFFER, TextureFormat.AARON_MOD$RGBA32F)
			.withVertexFormat(AaronModVertexFormats.POSITION_NORMAL, VertexFormat.Mode.LINES)
			.withDepthStencilState(Optional.empty())
			.build());
	public static final RenderPipeline LINES_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Main.id("pipeline/lines_through_walls"))
			.withDepthStencilState(Optional.empty())
			.build());
	public static final RenderPipeline BLURRED_RECTANGLE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
			.withLocation(Main.id("pipeline/blurred_rectangle"))
			.withVertexShader("core/position_color")
			.withFragmentShader(Main.id("core/box_blur"))
			.withSampler("Sampler0")
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			.build());

	@Init
	public static void init() {}
}

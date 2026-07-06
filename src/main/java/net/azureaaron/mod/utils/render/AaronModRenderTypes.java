package net.azureaaron.mod.utils.render;

import java.util.function.Function;

import net.minecraft.util.Util;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public class AaronModRenderTypes {
	private static final Function<Identifier, RenderType> CHROMA_TEXT = Util.memoize(
			texture -> RenderType.create(
					"chroma_text", RenderSetup.builder(AaronModRenderPipelines.CHROMA_TEXT).withTexture("Sampler0", texture).useLightmap().createRenderSetup()
					)
			);
	private static final Function<Identifier, RenderType> CHROMA_TEXT_GREYSCALE = Util.memoize(
			texture -> RenderType.create(
					"chroma_text_greyscale", RenderSetup.builder(AaronModRenderPipelines.CHROMA_TEXT_GREYSCALE).withTexture("Sampler0", texture).useLightmap().createRenderSetup()
					)
			);
	private static final Function<Identifier, RenderType> CHROMA_TEXT_POLYGON_OFFSET = Util.memoize(
			texture -> RenderType.create(
					"chroma_text_polygon_offset",
					RenderSetup.builder(AaronModRenderPipelines.CHROMA_TEXT_POLYGON_OFFSET).withTexture("Sampler0", texture).useLightmap().sortOnUpload().createRenderSetup()
					)
			);
	private static final Function<Identifier, RenderType> CHROMA_TEXT_GREYSCALE_POLYGON_OFFSET = Util.memoize(
			texture -> RenderType.create(
					"chroma_text_greyscale_polygon_offset",
					RenderSetup.builder(AaronModRenderPipelines.CHROMA_TEXT_GREYSCALE_POLYGON_OFFSET).withTexture("Sampler0", texture).useLightmap().sortOnUpload().createRenderSetup()
					)
			);
	private static final Function<Identifier, RenderType> CHROMA_TEXT_SEE_THROUGH = Util.memoize(
			texture -> RenderType.create(
					"chroma_text_see_through", RenderSetup.builder(AaronModRenderPipelines.CHROMA_TEXT_SEE_THROUGH).withTexture("Sampler0", texture).useLightmap().createRenderSetup()
					)
			);
	private static final Function<Identifier, RenderType> CHROMA_TEXT_GREYSCALE_SEE_THROUGH = Util.memoize(
			texture -> RenderType.create(
					"chroma_text_greyscale_see_through",
					RenderSetup.builder(AaronModRenderPipelines.CHROMA_TEXT_GREYSCALE_SEE_THROUGH).withTexture("Sampler0", texture).useLightmap().sortOnUpload().createRenderSetup()
					)
			);

	public static RenderType chromaText(Identifier id, boolean greyscale) {
		return greyscale ? CHROMA_TEXT_GREYSCALE.apply(id) : CHROMA_TEXT.apply(id);
	}

	public static RenderType chromaTextPolygonOffset(Identifier id, boolean greyscale) {
		return greyscale ? CHROMA_TEXT_GREYSCALE_POLYGON_OFFSET.apply(id) : CHROMA_TEXT_POLYGON_OFFSET.apply(id);
	}

	public static RenderType chromaTextSeeThrough(Identifier id, boolean greyscale) {
		return greyscale ? CHROMA_TEXT_GREYSCALE_SEE_THROUGH.apply(id) : CHROMA_TEXT_SEE_THROUGH.apply(id);
	}
}

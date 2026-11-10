package net.azureaaron.mod.utils.render.primitive;

import java.util.List;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;

import net.azureaaron.mod.utils.render.state.TextRenderState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.LightCoordsUtil;

public class TextFeatureRenderer extends PrimitiveFeatureRenderer<TextFeatureRenderer.Submit> {
	public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.create("Aaron's Mod Text");

	private static RenderPipeline getPipeline(boolean seeThrough, boolean greyscale) {
		if (seeThrough) {
			return greyscale ? RenderPipelines.TEXT_GRAYSCALE_SEE_THROUGH : RenderPipelines.TEXT_SEE_THROUGH;
		} else {
			return greyscale ? RenderPipelines.TEXT_GRAYSCALE : RenderPipelines.TEXT;
		}
	}

	@Override
	protected void buildGroup(FeatureFrameContext context, List<Submit> submits) {
		for (Submit submit : submits) {
			for (TextRenderState state : submit.states()) {
				Matrix4f positionMatrix = new Matrix4f()
						.translate((float) (state.pos().x() - submit.camera().pos.x()), (float) (state.pos().y() - submit.camera().pos.y()), (float) (state.pos().z() - submit.camera().pos.z()))
						.rotate(submit.camera().orientation)
						.scale(state.scale(), -state.scale(), state.scale());

				state.glyphs().visit(new Font.GlyphVisitor() {
					@Override
					public void acceptRenderable(TextRenderable renderable) {
						TextureSetup textureSetup = TextureSetup.singleTextureWithLightmap(renderable.textureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
						VertexConsumer builder = TextFeatureRenderer.this.getVertexBuilder(getPipeline(submit.throughWalls(), renderable.guiPipeline() == RenderPipelines.GUI_TEXT_GRAYSCALE), textureSetup);

						renderable.render(positionMatrix, builder, LightCoordsUtil.FULL_BRIGHT, false);
					}
				});
			}
		}
	}

	public record Submit(List<TextRenderState> states, CameraRenderState camera, boolean throughWalls) implements SubmitNode {
		@Override
		public FeatureRendererType<? extends SubmitNode> featureType() {
			return TYPE;
		}
	}
}

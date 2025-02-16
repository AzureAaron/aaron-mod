package net.azureaaron.mod.utils.render;

import java.util.OptionalDouble;
import java.util.function.DoubleFunction;

import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayer.MultiPhase;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase.Cull;
import net.minecraft.client.render.RenderPhase.DepthTest;
import net.minecraft.client.render.RenderPhase.Layering;
import net.minecraft.client.render.RenderPhase.LineWidth;
import net.minecraft.client.render.RenderPhase.ShaderProgram;
import net.minecraft.client.render.RenderPhase.Transparency;
import net.minecraft.client.render.RenderPhase.WriteMaskState;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;

public class AaronModRenderLayers {
	private static final Double2ObjectMap<MultiPhase> LINES_RENDER_LAYERS = new Double2ObjectOpenHashMap<>();

	public static final MultiPhase FILLED_BOX = RenderLayer.of("filled_box", VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP, RenderLayer.CUTOUT_BUFFER_SIZE, false, true, MultiPhaseParameters.builder()
			.program(ShaderProgram.POSITION_COLOR_PROGRAM)
			.layering(Layering.VIEW_OFFSET_Z_LAYERING)
			.transparency(Transparency.TRANSLUCENT_TRANSPARENCY)
			.depthTest(DepthTest.LEQUAL_DEPTH_TEST)
			.build(false));

	private static final DoubleFunction<MultiPhase> LINES = lineWidth -> RenderLayer.of("lines", VertexFormats.LINES, DrawMode.LINES, RenderLayer.CUTOUT_BUFFER_SIZE, false, false, MultiPhaseParameters.builder()
			.program(ShaderProgram.LINES_PROGRAM)
			.lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
			.layering(Layering.VIEW_OFFSET_Z_LAYERING)
			.transparency(Transparency.TRANSLUCENT_TRANSPARENCY)
			.writeMaskState(WriteMaskState.ALL_MASK)
			.cull(Cull.DISABLE_CULLING)
			.depthTest(DepthTest.LEQUAL_DEPTH_TEST)
			.build(false));

	public static MultiPhase getLines(double lineWidth) {
		return LINES_RENDER_LAYERS.computeIfAbsent(lineWidth, LINES);
	}
}

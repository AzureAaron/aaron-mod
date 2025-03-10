package net.azureaaron.mod.utils.render;

import java.util.OptionalDouble;
import java.util.function.DoubleFunction;

import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayer.MultiPhase;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.RenderPhase.LineWidth;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;

public class AaronModRenderLayers {
	private static final Double2ObjectMap<MultiPhase> LINES_RENDER_LAYERS = new Double2ObjectOpenHashMap<>();

	public static final MultiPhase FILLED_BOX = RenderLayer.of("filled_box", VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP, RenderLayer.DEFAULT_BUFFER_SIZE, false, true, MultiPhaseParameters.builder()
			.program(RenderPhase.POSITION_COLOR_PROGRAM)
			.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
			.transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
			.depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
			.build(false));

	private static final DoubleFunction<MultiPhase> LINES = lineWidth -> RenderLayer.of("lines", VertexFormats.LINES, DrawMode.LINES, RenderLayer.DEFAULT_BUFFER_SIZE, false, false, MultiPhaseParameters.builder()
			.program(RenderPhase.LINES_PROGRAM)
			.lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
			.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
			.transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
			.writeMaskState(RenderPhase.ALL_MASK)
			.cull(RenderPhase.DISABLE_CULLING)
			.depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
			.build(false));

	public static MultiPhase getLines(double lineWidth) {
		return LINES_RENDER_LAYERS.computeIfAbsent(lineWidth, LINES);
	}
}

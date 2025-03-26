package net.azureaaron.mod.utils.render;

import java.util.OptionalDouble;
import java.util.function.DoubleFunction;

import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayer.MultiPhase;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.RenderPhase.LineWidth;

public class AaronModRenderLayers {
	private static final Double2ObjectMap<MultiPhase> LINES_RENDER_LAYERS = new Double2ObjectOpenHashMap<>();

	public static final MultiPhase FILLED_BOX = RenderLayer.of("filled_box", RenderLayer.DEFAULT_BUFFER_SIZE, false, true, RenderPipelines.DEBUG_FILLED_BOX, MultiPhaseParameters.builder()
			.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
			.build(false));

	private static final DoubleFunction<MultiPhase> LINES = lineWidth -> RenderLayer.of("lines", RenderLayer.DEFAULT_BUFFER_SIZE, false, false, RenderPipelines.LINES, MultiPhaseParameters.builder()
			.lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
			.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
			.build(false));

	public static MultiPhase getLines(double lineWidth) {
		return LINES_RENDER_LAYERS.computeIfAbsent(lineWidth, LINES);
	}
}

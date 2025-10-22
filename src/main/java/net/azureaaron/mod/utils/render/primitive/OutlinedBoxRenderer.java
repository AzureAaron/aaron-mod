package net.azureaaron.mod.utils.render.primitive;

import org.joml.Matrix4f;

import net.azureaaron.mod.utils.render.AaronModRenderPipelines;
import net.azureaaron.mod.utils.render.MatrixHelper;
import net.azureaaron.mod.utils.render.Renderer;
import net.azureaaron.mod.utils.render.state.CameraRenderState;
import net.azureaaron.mod.utils.render.state.OutlinedBoxRenderState;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;

public final class OutlinedBoxRenderer implements PrimitiveRenderer<OutlinedBoxRenderState> {
	protected static final OutlinedBoxRenderer INSTANCE = new OutlinedBoxRenderer();

	private OutlinedBoxRenderer() {}

	@Override
	public void submitPrimitives(OutlinedBoxRenderState state, CameraRenderState cameraState) {
		BufferBuilder buffer = Renderer.getBuffer(state.throughWalls ? AaronModRenderPipelines.LINES_THROUGH_WALLS : RenderPipelines.LINES, state.lineWidth);
		Matrix4f positionMatrix = new Matrix4f()
				.translate((float) -cameraState.pos.x, (float) -cameraState.pos.y, (float) -cameraState.pos.z);
		MatrixStack matrices = MatrixHelper.toStack(positionMatrix);

		VertexRendering.drawBox(matrices, buffer, state.minX, state.minY, state.minZ, state.maxX, state.maxY, state.maxZ, state.colourComponents[0], state.colourComponents[1], state.colourComponents[2], state.alpha);
	}
}

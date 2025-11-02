package net.azureaaron.mod.utils.render.primitive;

import java.util.ArrayList;
import java.util.List;

import net.azureaaron.mod.utils.render.FrustumUtils;
import net.azureaaron.mod.utils.render.state.FilledBoxRenderState;
import net.azureaaron.mod.utils.render.state.OutlinedBoxRenderState;
import net.azureaaron.mod.utils.render.state.TextRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class PrimitiveCollectorImpl implements PrimitiveCollector {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	protected static final int MAX_OVERWORLD_BUILD_HEIGHT = 319;
	@SuppressWarnings("unused")
	private final WorldRenderState worldState;
	private final Frustum frustum;
	private List<FilledBoxRenderState> filledBoxStates = null;
	private List<OutlinedBoxRenderState> outlinedBoxStates = null;
	private List<TextRenderState> textStates = null;
	private boolean frozen = false;

	public PrimitiveCollectorImpl(WorldRenderState worldState, Frustum frustum) {
		this.worldState = worldState;
		this.frustum = frustum;
	}

	@Override
	public void submitFilledBox(BlockPos pos, float[] colourComponents, float alpha, boolean throughWalls) {
		submitFilledBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, colourComponents, alpha, throughWalls);
	}

	@Override
	public void submitFilledBox(Vec3d pos, Vec3d dimensions, float[] colourComponents, float alpha, boolean throughWalls) {
		submitFilledBox(pos.x, pos.y, pos.z, pos.x + dimensions.x, pos.y + dimensions.y, pos.z + dimensions.z, colourComponents, alpha, throughWalls);
	}

	@Override
	public void submitFilledBox(Box box, float[] colourComponents, float alpha, boolean throughWalls) {
		submitFilledBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, colourComponents, alpha, throughWalls);
	}

	private void submitFilledBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colourComponents, float alpha, boolean throughWalls) {
		ensureNotFrozen();

		// Ensure the box is in view
		if (!FrustumUtils.isVisible(this.frustum, minX, minY, minZ, maxX, maxY, maxZ)) {
			return;
		}

		if (this.filledBoxStates == null) {
			this.filledBoxStates = new ArrayList<>();
		}

		FilledBoxRenderState state = new FilledBoxRenderState();
		state.minX = minX;
		state.minY = minY;
		state.minZ = minZ;
		state.maxX = maxX;
		state.maxY = maxY;
		state.maxZ = maxZ;
		state.colourComponents = colourComponents;
		state.alpha = alpha;
		state.throughWalls = throughWalls;

		this.filledBoxStates.add(state);
	}

	@Override
	public void submitOutlinedBox(Box box, float[] colourComponents, float alpha, float lineWidth, boolean throughWalls) {
		submitOutlinedBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, colourComponents, alpha, lineWidth, throughWalls);
	}

	private void submitOutlinedBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colourComponents, float alpha, float lineWidth, boolean throughWalls) {
		ensureNotFrozen();

		// Ensure the box is in view
		if (!FrustumUtils.isVisible(this.frustum, minX, minY, minZ, maxX, maxY, maxZ)) {
			return;
		}

		if (this.outlinedBoxStates == null) {
			this.outlinedBoxStates = new ArrayList<>();
		}

		OutlinedBoxRenderState state = new OutlinedBoxRenderState();
		state.minX = minX;
		state.minY = minY;
		state.minZ = minZ;
		state.maxX = maxX;
		state.maxY = maxY;
		state.maxZ = maxZ;
		state.colourComponents = colourComponents;
		state.alpha = alpha;
		state.lineWidth = lineWidth;
		state.throughWalls = throughWalls;

		this.outlinedBoxStates.add(state);
	}

	@Override
	public void submitText(Text text, Vec3d pos, boolean throughWalls) {
		submitText(text, pos, 1, throughWalls);
	}

	@Override
	public void submitText(Text text, Vec3d pos, float scale, boolean throughWalls) {
		submitText(text, pos, scale, 0, throughWalls);
	}

	@Override
	public void submitText(Text text, Vec3d pos, float scale, float yOffset, boolean throughWalls) {
		submitText(text.asOrderedText(), pos, scale, yOffset, throughWalls);
	}

	private void submitText(OrderedText text, Vec3d pos, float scale, float yOffset, boolean throughWalls) {
		ensureNotFrozen();

		if (this.textStates == null) {
			this.textStates = new ArrayList<>();
		}

		TextRenderer textRenderer = CLIENT.textRenderer;
		float xOffset = -textRenderer.getWidth(text) / 2f;
		TextRenderer.GlyphDrawable glyphs = textRenderer.prepare(text, xOffset, yOffset, Colors.WHITE, false, 0);

		TextRenderState state = new TextRenderState();
		state.glyphs = glyphs;
		state.pos = pos;
		state.scale = scale * 0.025f;
		state.yOffset = yOffset;
		state.throughWalls = throughWalls;

		this.textStates.add(state);
	}

	public void endCollection() {
		this.frozen = true;
	}

	/**
	 * Instances of this class are used only once, and primitives should not be submitted once the collection phase has ended.
	 */
	private void ensureNotFrozen() {
		if (this.frozen) {
			throw new IllegalStateException("Cannot submit primitives once the collection phase has ended!");
		}
	}

	public void dispatchPrimitivesToRenderers(CameraRenderState cameraState) {
		if (!this.frozen) {
			throw new IllegalStateException("Cannot dispatch primitives until the collection phase has ended!");
		}

		if (this.filledBoxStates != null) {
			for (FilledBoxRenderState state : this.filledBoxStates) {
				FilledBoxRenderer.INSTANCE.submitPrimitives(state, cameraState);
			}
		}

		if (this.outlinedBoxStates != null) {
			for (OutlinedBoxRenderState state : this.outlinedBoxStates) {
				OutlinedBoxRenderer.INSTANCE.submitPrimitives(state, cameraState);
			}
		}

		if (this.textStates != null) {
			for (TextRenderState state : this.textStates) {
				TextPrimitiveRenderer.INSTANCE.submitPrimitives(state, cameraState);
			}
		}
	}
}

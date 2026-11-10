package net.azureaaron.mod.utils.render.primitive;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.utils.render.FrustumUtils;
import net.azureaaron.mod.utils.render.state.FilledBoxRenderState;
import net.azureaaron.mod.utils.render.state.OutlinedBoxRenderState;
import net.azureaaron.mod.utils.render.state.TextRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.SubmitRenderPhases;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class PrimitiveCollectorImpl implements PrimitiveCollector {
	private static final Minecraft MINECRAFT = Minecraft.getInstance();
	protected static final int MAX_OVERWORLD_BUILD_HEIGHT = 319;
	private final boolean isVulkan;
	@SuppressWarnings("unused")
	private final LevelRenderState worldState;
	private final Frustum frustum;
	private final List<FilledBoxRenderState> filledBoxStates = new ArrayList<>();
	private final List<FilledBoxRenderState> filledBoxThroughWallsStates = new ArrayList<>();
	private final List<OutlinedBoxRenderState> outlinedBoxStates = new ArrayList<>();
	private final List<OutlinedBoxRenderState> outlinedBoxThroughWallsStates = new ArrayList<>();
	private final List<TextRenderState> textStates = new ArrayList<>();
	private final List<TextRenderState> textThroughWallsStates = new ArrayList<>();
	private boolean frozen = false;

	public PrimitiveCollectorImpl(LevelRenderState worldState, Frustum frustum) {
		this.isVulkan = RenderSystem.getDevice().getDeviceInfo().backendName().equalsIgnoreCase("vulkan");
		this.worldState = worldState;
		this.frustum = frustum;
	}

	@Override
	public void submitFilledBox(BlockPos pos, float[] colourComponents, float alpha, boolean throughWalls) {
		submitFilledBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, colourComponents, alpha, throughWalls);
	}

	@Override
	public void submitFilledBox(Vec3 pos, Vec3 dimensions, float[] colourComponents, float alpha, boolean throughWalls) {
		submitFilledBox(pos.x, pos.y, pos.z, pos.x + dimensions.x, pos.y + dimensions.y, pos.z + dimensions.z, colourComponents, alpha, throughWalls);
	}

	@Override
	public void submitFilledBox(AABB box, float[] colourComponents, float alpha, boolean throughWalls) {
		submitFilledBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, colourComponents, alpha, throughWalls);
	}

	private void submitFilledBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colourComponents, float alpha, boolean throughWalls) {
		ensureNotFrozen();

		// Ensure the box is in view
		if (!FrustumUtils.isVisible(this.frustum, minX, minY, minZ, maxX, maxY, maxZ)) {
			return;
		}

		FilledBoxRenderState state = new FilledBoxRenderState(minX, minY, minZ, maxX, maxY, maxZ, colourComponents, alpha);

		if (throughWalls) {
			this.filledBoxThroughWallsStates.add(state);
		} else {
			this.filledBoxStates.add(state);
		}
	}

	@Override
	public void submitOutlinedBox(AABB box, float[] colourComponents, float alpha, float lineWidth, boolean throughWalls) {
		submitOutlinedBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, colourComponents, alpha, lineWidth, throughWalls);
	}

	private void submitOutlinedBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float[] colourComponents, float alpha, float lineWidth, boolean throughWalls) {
		ensureNotFrozen();

		// Ensure the box is in view
		if (!FrustumUtils.isVisible(this.frustum, minX, minY, minZ, maxX, maxY, maxZ)) {
			return;
		}

		OutlinedBoxRenderState state = new OutlinedBoxRenderState(minX, minY, minZ, maxX, maxY, maxZ, colourComponents, alpha, lineWidth);

		if (throughWalls) {
			this.outlinedBoxThroughWallsStates.add(state);
		} else {
			this.outlinedBoxStates.add(state);
		}
	}

	@Override
	public void submitText(Component text, Vec3 pos, boolean throughWalls) {
		submitText(text, pos, 1, throughWalls);
	}

	@Override
	public void submitText(Component text, Vec3 pos, float scale, boolean throughWalls) {
		submitText(text, pos, scale, 0, throughWalls);
	}

	@Override
	public void submitText(Component text, Vec3 pos, float scale, float yOffset, boolean throughWalls) {
		submitText(text.getVisualOrderText(), pos, scale, yOffset, throughWalls);
	}

	private void submitText(FormattedCharSequence text, Vec3 pos, float scale, float yOffset, boolean throughWalls) {
		ensureNotFrozen();

		Font textRenderer = MINECRAFT.font;
		float xOffset = -textRenderer.width(text) / 2f;
		Font.PreparedText glyphs = textRenderer.prepareText(text, xOffset, yOffset, CommonColors.WHITE, false, false, 0);

		TextRenderState state = new TextRenderState(glyphs, pos, scale * 0.025f, yOffset);

		if (throughWalls) {
			this.textThroughWallsStates.add(state);
		} else {
			this.textStates.add(state);
		}
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

	public void dispatchSubmits(LevelRenderState levelState, SubmitNodeCollector submitNodeCollector) {
		if (!this.frozen) {
			throw new IllegalStateException("Cannot dispatch primitives until the collection phase has ended!");
		}

		CameraRenderState camera = levelState.cameraRenderState;

		if (!this.filledBoxStates.isEmpty()) {
			if (this.isVulkan) {
				submitNodeCollector.submitCustom(SubmitRenderPhases.AFTER_TERRAIN, new FilledBoxInstancedFeatureRenderer.Submit(this.filledBoxStates, camera, false));
			} else {
				submitNodeCollector.submitCustom(SubmitRenderPhases.AFTER_TERRAIN, new FilledBoxFeatureRenderer.Submit(this.filledBoxStates, camera, false));
			}
		}

		if (!this.filledBoxThroughWallsStates.isEmpty()) {
			if (this.isVulkan) {
				submitNodeCollector.submitCustom(SubmitRenderPhases.ALWAYS_ON_TOP, new FilledBoxInstancedFeatureRenderer.Submit(this.filledBoxThroughWallsStates, camera, true));
			} else {
				submitNodeCollector.submitCustom(SubmitRenderPhases.ALWAYS_ON_TOP, new FilledBoxFeatureRenderer.Submit(this.filledBoxThroughWallsStates, camera, true));
			}
		}

		if (!this.outlinedBoxStates.isEmpty()) {
			if (this.isVulkan) {
				submitNodeCollector.submitCustom(SubmitRenderPhases.AFTER_TERRAIN, new OutlinedBoxInstancedFeatureRenderer.Submit(this.outlinedBoxStates, camera, false));
			} else {
				submitNodeCollector.submitCustom(SubmitRenderPhases.AFTER_TERRAIN, new OutlinedBoxFeatureRenderer.Submit(this.outlinedBoxStates, camera, false));
			}
		}

		if (!this.outlinedBoxThroughWallsStates.isEmpty()) {
			if (this.isVulkan) {
				submitNodeCollector.submitCustom(SubmitRenderPhases.ALWAYS_ON_TOP, new OutlinedBoxInstancedFeatureRenderer.Submit(this.outlinedBoxThroughWallsStates, camera, true));
			} else {
				submitNodeCollector.submitCustom(SubmitRenderPhases.ALWAYS_ON_TOP, new OutlinedBoxFeatureRenderer.Submit(this.outlinedBoxThroughWallsStates, camera, true));
			}
		}

		if (!this.textStates.isEmpty()) {
			submitNodeCollector.submitCustom(SubmitRenderPhases.TEXTS, new TextFeatureRenderer.Submit(this.textStates, camera, false));
		}

		if (!this.textThroughWallsStates.isEmpty()) {
			submitNodeCollector.submitCustom(SubmitRenderPhases.ALWAYS_ON_TOP, new TextFeatureRenderer.Submit(this.textThroughWallsStates, camera, true));
		}
	}
}

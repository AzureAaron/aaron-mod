package net.azureaaron.mod.utils.render.hud;

import java.util.Objects;
import java.util.function.IntConsumer;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.gui.DrawContext;

/**
 * An abstract representation of a HUD element. Extend this or use built-in subclasses to implement HUD elements.
 * 
 * Note that this currently only covers the configuration aspect of the HUD element and not the actual rendering
 * of it to the HUD.
 */
public abstract class HudElement {
	private final IntConsumer xConsumer;
	private final IntConsumer yConsumer;
	private final FloatConsumer scaleConsumer;
	private final int defaultX;
	private final int defaultY;

	private int x;
	private int y;
	private float scale;

	protected HudElement(
			int x,
			int y,
			float scale,
			IntConsumer xConsumer,
			IntConsumer yConsumer,
			FloatConsumer scaleConsumer,
			int defaultX,
			int defaultY
	) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.xConsumer = Objects.requireNonNull(xConsumer, "X Consumer cannot be null");
		this.yConsumer = Objects.requireNonNull(yConsumer, "Y Consumer cannot be null");
		this.scaleConsumer = Objects.requireNonNull(scaleConsumer, "Scale Consumer cannot be null");
		this.defaultX = defaultX;
		this.defaultY = defaultY;

		//Register element with config
		HudElementConfigScreen.register(this);
	}

	public int x() {
		return x;
	}

	public void x(int x) {
		this.x = x;
	}

	public int y() {
		return y;
	}

	public void y(int y) {
		this.y = y;
	}

	public float scale() {
		return scale;
	}

	public void scale(float scale) {
		this.scale = scale;
	}

	public abstract int width();

	public abstract int height();

	public void apply() {
		xConsumer.accept(x);
		yConsumer.accept(y);
		scaleConsumer.accept(scale);
	}

	public void reset() {
		x = defaultX;
		y = defaultY;
		scale = 1f;
	}

	/**
	 * @apiNote The implementation of the rendering for each HUD element in the config screen should be
	 * the exact same as the rendering of the element to the actual HUD itself.
	 */
	public abstract void render(DrawContext context);
}

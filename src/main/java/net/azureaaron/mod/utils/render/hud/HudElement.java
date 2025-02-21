package net.azureaaron.mod.utils.render.hud;

import java.util.Objects;

import net.minecraft.client.gui.DrawContext;

/**
 * An abstract representation of a HUD element. Extend this or use built-in subclasses to implement HUD elements.
 * 
 * Note that this currently only covers the configuration aspect of the HUD element and not the actual rendering
 * of it to the HUD.
 */
public abstract class HudElement {
	protected final HudElementAccess access;
	private final int defaultX;
	private final int defaultY;

	private int x;
	private int y;
	private float scale;

	protected HudElement(
			HudElementAccess access,
			int defaultX,
			int defaultY
	) {
		this.access = Objects.requireNonNull(access, "Access cannot be null!");
		this.defaultX = defaultX;
		this.defaultY = defaultY;
		this.x = access.x();
		this.y = access.y();
		this.scale = access.scale();

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
		access.x(x);
		access.y(y);
		access.scale(scale);
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

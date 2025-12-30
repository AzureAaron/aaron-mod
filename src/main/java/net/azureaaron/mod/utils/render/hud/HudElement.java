package net.azureaaron.mod.utils.render.hud;

import java.util.Objects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/**
 * An abstract representation of a HUD element. Extend this or use built-in subclasses to implement HUD elements.
 *
 * Note that this currently only covers the configuration aspect of the HUD element and not the actual rendering
 * of it to the HUD.
 */
public abstract class HudElement {
	protected static final Minecraft CLIENT = Minecraft.getInstance();
	protected final HudElementAccess access;
	private final int defaultX;
	private final int defaultY;

	private int x;
	private int y;
	private float scale;

	protected HudElement(HudElementAccess access, int defaultX, int defaultY) {
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
	 * Returns whether this element should be rendered onto the HUD.
	 */
	protected boolean shouldRender() {
		return !CLIENT.getDebugOverlay().showDebugScreen() && access.shouldRender();
	}

	/**
	 * Used for rendering this element to a {@link net.minecraft.client.gui.screens.Screen Screen}.
	 */
	public abstract void renderScreen(GuiGraphics context);

	/**
	 * Used for rendering this element to the HUD with Fabric's HUD layer rendering system.
	 *
	 * @implSpec The signature of this method must match the {@link net.minecraft.client.gui.LayeredDrawer.Layer#render(DrawContext, RenderTickCounter) LayeredDrawer$Layer#render} method.
	 */
	public abstract void renderHud(GuiGraphics context, DeltaTracker tickCounter);
}

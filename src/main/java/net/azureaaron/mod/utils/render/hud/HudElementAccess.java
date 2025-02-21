package net.azureaaron.mod.utils.render.hud;

/**
 * Provides access to the HUD Element's config to manage its properties.
 */
public interface HudElementAccess {

	int x();

	void x(int x);

	int y();

	void y (int y);

	float scale();

	void scale(float scale);
}

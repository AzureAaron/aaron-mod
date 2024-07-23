package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.azureaaron.mod.injected.ScreenResizeMarker;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public class ScreenMixin implements ScreenResizeMarker {
	@Unique
	private boolean screenResized;

	@Override
	public void markResized(boolean resized) {
		this.screenResized = resized;
	}

	@Override
	public boolean wasResized() {
		return this.screenResized;
	}
}

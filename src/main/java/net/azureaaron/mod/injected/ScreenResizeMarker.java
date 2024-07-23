package net.azureaaron.mod.injected;

public interface ScreenResizeMarker {

	default void markResized(boolean resized) {
	}

	default boolean wasResized() {
		return false;
	}
}

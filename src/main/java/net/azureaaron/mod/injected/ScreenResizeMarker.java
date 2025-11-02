package net.azureaaron.mod.injected;

public interface ScreenResizeMarker {

	default void markResized(boolean resized) {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}

	default boolean wasResized() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}

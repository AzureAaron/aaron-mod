package net.azureaaron.mod.injected;

public interface NativeImageMarker {

	default void markScreenshot() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}

package net.azureaaron.mod.injected;

public interface MouseGuiPositioner {

	default void resetMousePos() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}

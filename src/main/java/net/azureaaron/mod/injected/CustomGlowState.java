package net.azureaaron.mod.injected;

public interface CustomGlowState {

	default void aaronMod$markCustomGlow() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}

	default boolean aaronMod$hasCustomGlow() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}

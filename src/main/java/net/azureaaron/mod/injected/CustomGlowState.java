package net.azureaaron.mod.injected;

public interface CustomGlowState {

	default void aaronMod$setCustomGlowColour(int glowColour) {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}

	default int aaronMod$getCustomGlowColour() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}

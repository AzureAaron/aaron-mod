package net.azureaaron.mod.injected;

public interface ParticleAlphaMarker {

	default void markHasCustomAlpha() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}

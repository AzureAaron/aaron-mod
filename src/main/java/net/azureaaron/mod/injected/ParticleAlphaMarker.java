package net.azureaaron.mod.injected;

public interface ParticleAlphaMarker {

	default void markHasCustomAlpha() {	
	}

	default boolean hasCustomAlpha() {
		return false;
	}
}

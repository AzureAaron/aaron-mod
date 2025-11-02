package net.azureaaron.mod.injected;

public interface AaronModItemMeta {

	default boolean getAlwaysDisplaySkyblockInfo() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}

	default void setAlwaysDisplaySkyblockInfo(boolean value) {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}

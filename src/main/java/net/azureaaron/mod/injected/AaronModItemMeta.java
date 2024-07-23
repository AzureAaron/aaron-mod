package net.azureaaron.mod.injected;

public interface AaronModItemMeta {

	default boolean getAlwaysDisplaySkyblockInfo() {
		return false;
	}

	default void setAlwaysDisplaySkyblockInfo(boolean value) {
	}
}

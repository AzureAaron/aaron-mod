package net.azureaaron.mod.injected;

public interface GuiDepthStateTracker {

	default boolean shouldUseSavedGuiDepth() {
		return false;
	}
}

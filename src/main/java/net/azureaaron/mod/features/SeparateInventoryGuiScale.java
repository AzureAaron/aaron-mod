package net.azureaaron.mod.features;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.Window;

public class SeparateInventoryGuiScale {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public static boolean isEnabled(Screen screen) {
		return AaronModConfigManager.get().separateInventoryGuiScale && screen instanceof HandledScreen<?>;
	}

	private static int getInventoryGuiScale() {
		int configuredScale = AaronModConfigManager.get().inventoryGuiScale;

		if (configuredScale > 0) {
			return configuredScale;
		} else {
			return CLIENT.getWindow().calculateScaleFactor(0, CLIENT.forcesUnicodeFont());
		}
	}

	public record SavedScaleState(Window window, double originalScaleFactor) {

		public static SavedScaleState create(Window window) {
			return new SavedScaleState(window, window.getScaleFactor());
		}

		public SavedScaleState adjust() {
			window.setScaleFactor(window.calculateScaleFactor(getInventoryGuiScale(), CLIENT.forcesUnicodeFont()));

			return this;
		}

		public SavedScaleState reset() {
			window.setScaleFactor(originalScaleFactor);

			return this;
		}
	}
}

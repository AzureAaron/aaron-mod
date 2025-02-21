package net.azureaaron.mod.features;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.Window;

public class SeparateInventoryGuiScale {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	public static final int AUTO = 0;

	public static boolean isEnabled(Screen screen) {
		return AaronModConfigManager.get().uiAndVisuals.inventoryScreen.separateInventoryGuiScale && screen instanceof HandledScreen<?>;
	}

	private static int getInventoryGuiScale() {
		int configuredScale = AaronModConfigManager.get().uiAndVisuals.inventoryScreen.inventoryGuiScale;

		if (configuredScale > AUTO) {
			return configuredScale;
		} else {
			//Never returns 0 since this is always called when the client is running
			return getAutoGuiScale();
		}
	}

	public static int getAutoGuiScale() {
		return CLIENT.isRunning() ? CLIENT.getWindow().calculateScaleFactor(AUTO, CLIENT.forcesUnicodeFont()) : AUTO;
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

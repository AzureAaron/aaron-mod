package net.azureaaron.mod.features;

import com.mojang.blaze3d.platform.Window;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class SeparateInventoryGuiScale {
	private static final Minecraft CLIENT = Minecraft.getInstance();
	public static final int AUTO = 0;

	public static boolean isEnabled(Screen screen) {
		return AaronModConfigManager.get().uiAndVisuals.inventoryScreen.separateInventoryGuiScale && screen instanceof AbstractContainerScreen<?>;
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
		return CLIENT.isRunning() ? CLIENT.getWindow().calculateScale(AUTO, CLIENT.isEnforceUnicode()) : AUTO;
	}

	public record SavedScaleState(Window window, int originalScaleFactor) {

		public static SavedScaleState create(Window window) {
			return new SavedScaleState(window, window.getGuiScale());
		}

		public SavedScaleState adjust() {
			this.window.setGuiScale(this.window.calculateScale(getInventoryGuiScale(), CLIENT.isEnforceUnicode()));

			return this;
		}

		public SavedScaleState reset() {
			this.window.setGuiScale(this.originalScaleFactor);

			return this;
		}
	}
}

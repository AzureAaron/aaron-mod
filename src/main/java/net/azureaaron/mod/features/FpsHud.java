package net.azureaaron.mod.features;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.render.hud.HudElementAccess;
import net.azureaaron.mod.utils.render.hud.TextHudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class FpsHud {
	private static final Minecraft CLIENT = Minecraft.getInstance();
	private static final Identifier ID = Main.id("fps");
	private static final TextHudElement HUD_ELEMENT = new TextHudElement(
			Component.nullToEmpty("100 fps"),
			FpsHud::getFpsText,
			new FpsHudElementAccess(),
			2,
			2);

	@Init
	public static void init() {
		HudElementRegistry.attachElementAfter(VanillaHudElements.STATUS_EFFECTS, ID, HUD_ELEMENT::renderHud);
	}

	private static Component getFpsText() {
		return Component.literal(CLIENT.getFps() + " fps");
	}

	private record FpsHudElementAccess() implements HudElementAccess {
		@Override
		public int x() {
			return AaronModConfigManager.get().uiAndVisuals.fpsHud.x;
		}

		@Override
		public void x(int x) {
			AaronModConfigManager.updateOnly(config -> config.uiAndVisuals.fpsHud.x = x);
		}

		@Override
		public int y() {
			return AaronModConfigManager.get().uiAndVisuals.fpsHud.y;
		}

		@Override
		public void y(int y) {
			AaronModConfigManager.updateOnly(config -> config.uiAndVisuals.fpsHud.y = y);
		}

		@Override
		public float scale() {
			return AaronModConfigManager.get().uiAndVisuals.fpsHud.scale;
		}

		@Override
		public void scale(float scale) {
			AaronModConfigManager.updateOnly(config -> config.uiAndVisuals.fpsHud.scale = scale);
		}

		@Override
		public boolean shouldRender() {
			return AaronModConfigManager.get().uiAndVisuals.fpsHud.enableFpsHud;
		}
	}
}

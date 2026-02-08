package net.azureaaron.mod.features;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Formatters;
import net.azureaaron.mod.utils.ServerTickCounter;
import net.azureaaron.mod.utils.render.hud.HudElementAccess;
import net.azureaaron.mod.utils.render.hud.TextHudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class TickHud {
	private static final Identifier ID = Main.id("ticks");
	public static final int DEFAULT_X = 50;
	private static final TextHudElement HUD_ELEMENT = new TextHudElement(
			Component.nullToEmpty("20 tps"),
			TickHud::getTpsText,
			new TpsHudElementAccess(),
			DEFAULT_X,
			2
			);

	@Init
	public static void init() {
		HudElementRegistry.attachElementAfter(VanillaHudElements.STATUS_EFFECTS, ID, HUD_ELEMENT::renderHud);
	}

	private static Component getTpsText() {
		double tickRate = ServerTickCounter.INSTANCE.getTickRate();
		String formattedTickRate = tickRate > 0 ? Formatters.FLOAT_NUMBERS.format(tickRate) : "?";

		return Component.nullToEmpty(formattedTickRate + " tps");
	}

	private record TpsHudElementAccess() implements HudElementAccess {
		@Override
		public int x() {
			return AaronModConfigManager.get().uiAndVisuals.tpsHud.x;
		}

		@Override
		public void x(int x) {
			AaronModConfigManager.updateOnly(config -> config.uiAndVisuals.tpsHud.x = x);
		}

		@Override
		public int y() {
			return AaronModConfigManager.get().uiAndVisuals.tpsHud.y;
		}

		@Override
		public void y(int y) {
			AaronModConfigManager.updateOnly(config -> config.uiAndVisuals.tpsHud.y = y);
		}

		@Override
		public float scale() {
			return AaronModConfigManager.get().uiAndVisuals.tpsHud.scale;
		}

		@Override
		public void scale(float scale) {
			AaronModConfigManager.updateOnly(config -> config.uiAndVisuals.tpsHud.scale = scale);
		}

		@Override
		public boolean shouldRender() {
			return AaronModConfigManager.get().uiAndVisuals.tpsHud.enableTpsHud;
		}
	}
}

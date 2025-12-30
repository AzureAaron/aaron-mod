package net.azureaaron.mod.features;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Formatters;
import net.azureaaron.mod.utils.ServerTickCounter;
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
			AaronModConfigManager.get().uiAndVisuals.tpsHud,
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
}

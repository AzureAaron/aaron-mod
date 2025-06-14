package net.azureaaron.mod.features;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Formatters;
import net.azureaaron.mod.utils.ServerTickCounter;
import net.azureaaron.mod.utils.render.hud.TextHudElement;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TickHud {
	private static final Identifier ID = Identifier.of(Main.NAMESPACE, "ticks");
	public static final int DEFAULT_X = 50;
	private static final TextHudElement HUD_ELEMENT = new TextHudElement(
			Text.of("20 tps"),
			TickHud::getTpsText,
			AaronModConfigManager.get().uiAndVisuals.tpsHud,
			DEFAULT_X,
			2
			);

	@Init
	public static void init() {
		HudLayerRegistrationCallback.EVENT.register(d -> d.attachLayerAfter(IdentifiedLayer.STATUS_EFFECTS, ID, HUD_ELEMENT::renderHud));
	}

	private static Text getTpsText() {
		double tickRate = ServerTickCounter.INSTANCE.getTickRate();
		String formattedTickRate = tickRate > 0 ? Formatters.FLOAT_NUMBERS.format(tickRate) : "?";

		return Text.of(formattedTickRate + " tps");
	}
}

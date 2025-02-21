package net.azureaaron.mod.features;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.render.hud.TextHudElement;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FpsHud {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final Identifier ID = Identifier.of(Main.NAMESPACE, "fps");
	private static final TextHudElement HUD_ELEMENT = new TextHudElement(
			Text.of("100 fps"),
			FpsHud::getFpsText,
			AaronModConfigManager.get().uiAndVisuals.fpsHud,
			2,
			2);

	@Init
	public static void init() {
		HudLayerRegistrationCallback.EVENT.register(d -> d.attachLayerAfter(IdentifiedLayer.STATUS_EFFECTS, ID, HUD_ELEMENT::renderHud));
	}

	private static Text getFpsText() {
		return Text.literal(CLIENT.getCurrentFps() + " fps");
	}
}

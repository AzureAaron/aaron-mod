package net.azureaaron.mod.features;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.render.hud.TextHudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class FpsHud {
	private static final Minecraft CLIENT = Minecraft.getInstance();
	private static final Identifier ID = Identifier.fromNamespaceAndPath(Main.NAMESPACE, "fps");
	private static final TextHudElement HUD_ELEMENT = new TextHudElement(
			Component.nullToEmpty("100 fps"),
			FpsHud::getFpsText,
			AaronModConfigManager.get().uiAndVisuals.fpsHud,
			2,
			2);

	@Init
	public static void init() {
		HudElementRegistry.attachElementAfter(VanillaHudElements.STATUS_EFFECTS, ID, HUD_ELEMENT::renderHud);
	}

	private static Component getFpsText() {
		return Component.literal(CLIENT.getFps() + " fps");
	}
}

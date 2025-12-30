package net.azureaaron.mod.features;

import java.util.Queue;

import com.google.common.collect.EvictingQueue;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.PingResultCallback;
import net.azureaaron.mod.utils.Scheduler;
import net.azureaaron.mod.utils.render.hud.TextHudElement;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class PingHud {
	private static final Identifier ID = Identifier.fromNamespaceAndPath(Main.NAMESPACE, "ping");
	public static final int DEFAULT_Y = 2 + 9 + 4;
	private static final TextHudElement HUD_ELEMENT = new TextHudElement(
			Component.literal("30 ms"),
			PingHud::getPingText,
			AaronModConfigManager.get().uiAndVisuals.pingHud,
			2,
			DEFAULT_Y);
	private static final Queue<Long> RESULTS = EvictingQueue.create(240); //240 is the amount of samples that the ping graph uses
	private static long average = 0L;

	@Init
	public static void init() {
		HudElementRegistry.attachElementAfter(VanillaHudElements.STATUS_EFFECTS, ID, HUD_ELEMENT::renderHud);
		PingResultCallback.EVENT.register(RESULTS::offer);
		Scheduler.INSTANCE.scheduleCyclic(() -> average = (long) RESULTS.stream()
				.mapToLong(Long::longValue)
				.average()
				.orElse(0), 10);
		ClientPlayConnectionEvents.JOIN.register((_handler, _sender, _client) -> reset());
	}

	private static Component getPingText() {
		int colour;

		if (!AaronModConfigManager.get().uiAndVisuals.pingHud.colouredPing) {
			colour = 0xFFFFFF;
		} else if (average < 0) {
			colour = ChatFormatting.GRAY.getColor();
		} else if (average < 150) {
			colour = 0x00FF21;
		} else if (average < 300) {
			colour = 0xFBFF23;
		} else if (average < 600) {
			colour = 0xFF9028;
		} else if (average < 1000) {
			colour = 0xFF0000;
		} else {
			colour = 0x61007C;
		}

		return Component.empty().append(Component.literal(String.valueOf(average)).withColor(colour)).append(" ms");
	}

	private static void reset() {
		RESULTS.clear();
		average = 0L;
	}
}

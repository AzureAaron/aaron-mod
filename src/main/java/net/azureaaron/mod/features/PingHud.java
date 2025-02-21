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
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PingHud {
	private static final Identifier ID = Identifier.of(Main.NAMESPACE, "ping");
	public static final int DEFAULT_Y = 2 + 9 + 4;
	private static final TextHudElement HUD_ELEMENT = new TextHudElement(
			Text.literal("30 ms"),
			PingHud::getPingText,
			AaronModConfigManager.get().uiAndVisuals.pingHud,
			2,
			DEFAULT_Y);
	private static final Queue<Long> RESULTS = EvictingQueue.create(240); //240 is the amount of samples that the ping graph uses
	private static long average = 0L;

	@Init
	public static void init() {
		HudLayerRegistrationCallback.EVENT.register(d -> d.attachLayerAfter(IdentifiedLayer.STATUS_EFFECTS, ID, HUD_ELEMENT::renderHud));
		PingResultCallback.EVENT.register(RESULTS::offer);
		Scheduler.INSTANCE.scheduleCyclic(() -> average = (long) RESULTS.stream()
				.mapToLong(Long::longValue)
				.average()
				.orElse(0), 10);
		ClientPlayConnectionEvents.JOIN.register((_handler, _sender, _client) -> reset());
	}

	private static Text getPingText() {
		return Text.literal(average + " ms");
	}

	private static void reset() {
		RESULTS.clear();
		average = 0L;
	}
}

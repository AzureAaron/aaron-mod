package net.azureaaron.mod.features;

import java.util.Queue;

import com.google.common.collect.EvictingQueue;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.PingResultCallback;
import net.azureaaron.mod.utils.Scheduler;
import net.azureaaron.mod.utils.render.hud.TextHudElement;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class PingDisplay {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final Identifier ID = Identifier.of(Main.NAMESPACE, "ping");
	public static final int DEFAULT_Y = 2 + 9 + 4;
	@SuppressWarnings("unused")
	private static final TextHudElement HUD_ELEMENT = new TextHudElement(
			Text.literal("30 ms"),
			AaronModConfigManager.get().uiAndVisuals.pingHud,
			2,
			DEFAULT_Y);
	private static final Queue<Long> RESULTS = EvictingQueue.create(240); //240 is the amount of samples that the ping graph uses
	private static long average = 0L;

	@Init
	public static void init() {
		HudLayerRegistrationCallback.EVENT.register(d -> d.attachLayerAfter(IdentifiedLayer.STATUS_EFFECTS, ID, PingDisplay::render));
		PingResultCallback.EVENT.register(RESULTS::offer);
		Scheduler.INSTANCE.scheduleCyclic(() -> average = (long) RESULTS.stream()
				.mapToLong(Long::longValue)
				.average()
				.orElse(0), 10);
	}

	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		if (!CLIENT.getDebugHud().shouldShowDebugHud() && AaronModConfigManager.get().uiAndVisuals.pingHud.enablePingHud) {
			int x = AaronModConfigManager.get().uiAndVisuals.pingHud.x;
			int y = AaronModConfigManager.get().uiAndVisuals.pingHud.y;
			float scale = AaronModConfigManager.get().uiAndVisuals.pingHud.scale;
			MatrixStack matrices = context.getMatrices();

			matrices.push();
			matrices.scale(scale, scale, 0);

			context.drawText(CLIENT.textRenderer, average + " ms", (int) (x / scale), (int) (y / scale), Colors.WHITE, false);

			matrices.pop();
		}
	}
}

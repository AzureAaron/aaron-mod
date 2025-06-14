package net.azureaaron.mod.debug;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.utils.Scheduler;
import net.azureaaron.mod.utils.ServerTickCounter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.util.math.random.Random;

public class WorldDebug {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final Random RANDOM = Random.createLocal();
	private static final long DEBUG_WORLD_SEED = 8642631819104237334L;

	@Init
	public static void init() {
		if (Debug.debugEnabled()) {
			Scheduler.INSTANCE.scheduleCyclic(WorldDebug::simulateServerTicks, 1);
		}
	}

	/**
	 * Simulates the ping packets we use to calculate server ticks on hypixel.
	 */
	private static void simulateServerTicks() {
		if (getSeed() == DEBUG_WORLD_SEED) {
			CommonPingS2CPacket packet = new CommonPingS2CPacket(RANDOM.nextInt());

			for (int i = 0; i < 5; i++) {
				ServerTickCounter.INSTANCE.onServerTick(packet);
			}
		}
	}

	private static long getSeed() {
		return CLIENT.isIntegratedServerRunning() ? CLIENT.getServer().getOverworld().getSeed() : 0L;
	}
}

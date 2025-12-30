package net.azureaaron.mod.debug;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.utils.Scheduler;
import net.azureaaron.mod.utils.ServerTickCounter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.util.RandomSource;

public class WorldDebug {
	private static final Minecraft CLIENT = Minecraft.getInstance();
	private static final RandomSource RANDOM = RandomSource.createNewThreadLocalInstance();
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
			ClientboundPingPacket packet = new ClientboundPingPacket(RANDOM.nextInt());

			for (int i = 0; i < 5; i++) {
				ServerTickCounter.INSTANCE.onServerTick(packet);
			}
		}
	}

	private static long getSeed() {
		return CLIENT.hasSingleplayerServer() ? CLIENT.getSingleplayerServer().overworld().getSeed() : 0L;
	}
}

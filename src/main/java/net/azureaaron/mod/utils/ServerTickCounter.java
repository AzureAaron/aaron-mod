package net.azureaaron.mod.utils;

import java.util.Queue;

import com.google.common.collect.EvictingQueue;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.events.ServerTickCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;

public class ServerTickCounter {
	public static final ServerTickCounter INSTANCE = new ServerTickCounter();
	public static final long MILLIS_PER_TICK = 1000L / 20L;
	private final Queue<Integer> tpsResults = EvictingQueue.create(12);
	/**
	 * How many total ticks have passed.
	 */
	private int totalTicks;
	/**
	 * The last {@code parameter} value from the ping packet.
	 */
	private int lastParameter;
	/**
	 * Whether a packet was received during the current client tick.
	 */
	private boolean receivedPacketThisTick;
	/**
	 * How many client ticks within a second had a packet sent from the server processed by the client.
	 */
	private int ticksWithPacketsSent;
	/**
	 * The server's estimated tick rate
	 */
	private double tickRate;
	/**
	 * Used for tracking when we changed worlds to avoid calculating TPS until we have sufficient measurement data.
	 */
	private long lastWorldChange;

	@Init
	public static void init() {
		Scheduler.INSTANCE.scheduleCyclic(INSTANCE::onClientTick, 1);
		Scheduler.INSTANCE.scheduleCyclic(INSTANCE::calculateTickRate, 20);
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> INSTANCE.reset());
	}

	public void onServerTick(CommonPingS2CPacket packet) {
		//Make sure we don't count the same packet twice (if Hypixel sends it for some reason)
		if (packet.getParameter() != this.lastParameter) {
			this.totalTicks++;
			this.lastParameter = packet.getParameter();

			ServerTickCallback.EVENT.invoker().onTick();
		}
	}

	/**
	 * Calculates the tick rate each second.
	 * 
	 * The tick rate is counted based on how many ticks the client received a packet on within a second,
	 * checking the ping packets is not great since that is more hypixel-specific and this allows it to
	 * work with other servers (mileage may vary).
	 */
	private void calculateTickRate() {
		if (this.ticksWithPacketsSent > 0) {
			this.tpsResults.offer(this.ticksWithPacketsSent);
		}

		//Calculating the tick rate after switching words is not accurate since there is not enough samples
		if (this.lastWorldChange + 5000L < System.currentTimeMillis()) {
			double tickRate = this.tpsResults.stream()
					.mapToInt(Integer::intValue)
					.average()
					.orElse(0d);
			this.tickRate = Math.clamp(Math.round(tickRate * 10d) / 10d, 0d, 20d);
			this.ticksWithPacketsSent = 0;
		}
	}

	/**
	 * Each client tick, if we processed a packet then mark that as so.
	 */
	private void onClientTick() {
		if (this.receivedPacketThisTick) {
			this.ticksWithPacketsSent++;
			this.receivedPacketThisTick = false;
		}
	}

	public void onReceivePacket() {
		//This is called from outside the render thread and we need it to be on there otherwise
		//this won't work properly
		MinecraftClient.getInstance().send(() -> this.receivedPacketThisTick = true); 
	}

	private void reset() {
		this.tpsResults.clear();
		this.totalTicks = 0;
		this.lastParameter = 0;
		this.receivedPacketThisTick = false;
		this.ticksWithPacketsSent = 0;
		this.tickRate = 0d;
		this.lastWorldChange = System.currentTimeMillis();
	}

	public int getTotalTicks() {
		return this.totalTicks;
	}

	public double getTickRate() {
		return this.tickRate;
	}
}

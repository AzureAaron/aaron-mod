package net.azureaaron.mod.utils;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.azureaaron.hmapi.events.HypixelPacketEvents;
import net.azureaaron.hmapi.network.HypixelNetworking;
import net.azureaaron.hmapi.network.packet.s2c.ErrorS2CPacket;
import net.azureaaron.hmapi.network.packet.s2c.HelloS2CPacket;
import net.azureaaron.hmapi.network.packet.s2c.HypixelS2CPacket;
import net.azureaaron.hmapi.network.packet.v1.s2c.LocationUpdateS2CPacket;
import net.azureaaron.mod.annotations.Init;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.util.Util;

@SuppressWarnings("unused")
public class Utils {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static boolean isOnHypixel;
	private static boolean isOnSkyblock;
	private static String mode = ""; //Future Use

	@Init
	public static void init() {
		HypixelNetworking.registerToEvents(Util.make(new Object2IntOpenHashMap<>(), map -> map.put(LocationUpdateS2CPacket.ID, 1)));
		HypixelPacketEvents.HELLO.register(Utils::handlePacket);
		HypixelPacketEvents.LOCATION_UPDATE.register(Utils::handlePacket);
		//TODO set skyblock to false too
		ClientPlayConnectionEvents.DISCONNECT.register((_handler, _client) -> isOnHypixel = false);
	}

	private static void handlePacket(HypixelS2CPacket packet) {
		switch (packet) {
			case HelloS2CPacket(var _environment) -> {
				isOnHypixel = true;
			}

			case LocationUpdateS2CPacket(var _serverName, var serverType, var _lobbyName, var mode, var _map) -> {
				isOnSkyblock = serverType.orElse("").equals("SKYBLOCK");
				Utils.mode = mode.orElse("");
			}

			case ErrorS2CPacket(var id, var error) when id.equals(LocationUpdateS2CPacket.ID) -> {
				isOnSkyblock = false;
				mode = "";
				LOGGER.warn("[Aaron's Mod] Failed to update Hypixel location! Error: {}", error);
			}

			default -> {} //Do Nothing
		}
	}

	//Will probably migrate to using this in the future once it can be tested more
	public static boolean isOnHypixel() {
		return isOnHypixel;
	}

	public static boolean isOnSkyblock() {
		return isOnSkyblock;
	}
}

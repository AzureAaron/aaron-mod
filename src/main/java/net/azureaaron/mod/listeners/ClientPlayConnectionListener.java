package net.azureaaron.mod.listeners;

import java.util.Locale;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.utils.Cache;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class ClientPlayConnectionListener {

	@Init
	public static void init() {
		ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionListener::onJoin);
		ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionListener::onDisconnect);
	}

	private static void onJoin(ClientPacketListener handler, PacketSender sender, Minecraft client) {
		Cache.currentServerAddress = client.isLocalServer() || handler.getServerData().ip == null ? "localhost" : handler.getServerData().ip.toLowerCase(Locale.CANADA);
	}

	private static void onDisconnect(ClientPacketListener handler, Minecraft client) {
		Cache.lastServerAddress = Cache.currentServerAddress;
		Cache.currentServerAddress = "";
	}
}

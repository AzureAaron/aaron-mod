package net.azureaaron.mod.listeners;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.utils.Cache;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class ClientPlayConnectionListener {

	@Init
	public static void init() {
		ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionListener::onJoin);
		ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionListener::onDisconnect);
	}

	private static void onJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
		Cache.currentServerAddress = client.isInSingleplayer() || handler.getServerInfo().address == null ? "localhost" : handler.getServerInfo().address.toLowerCase();
	}

	private static void onDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
		Cache.lastServerAddress = Cache.currentServerAddress;
		Cache.currentServerAddress = "";
	}
}

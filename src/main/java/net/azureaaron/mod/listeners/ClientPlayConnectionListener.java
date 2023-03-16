package net.azureaaron.mod.listeners;

import net.azureaaron.mod.util.Cache;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class ClientPlayConnectionListener {
	
	public static void onJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
		Cache.currentServerAddress = client.isInSingleplayer() ? "localhost" : handler.getServerInfo().address;
		
		if(!Cache.lastServerAddress.equals(Cache.currentServerAddress)) {
			Cache.warningLevel = 0;
			Cache.lastShriekTime = 0L;
		}
	}
	
	public static void onDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
		Cache.lastServerAddress = Cache.currentServerAddress;
		Cache.currentServerAddress = "";
	}
}

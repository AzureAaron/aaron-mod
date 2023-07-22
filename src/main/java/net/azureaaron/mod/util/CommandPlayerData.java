package net.azureaaron.mod.util;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

/**
 * Record which holds a player's name and uuid, similar to a {@link com.mojang.authlib.GameProfile GameProfile}.
 * This is used for commands that take in a player argument.
 * 
 * @author Aaron
 */
public record CommandPlayerData(String name, String uuid) {
	
	/**
	 * Ensures that "dummy" players aren't included in command suggestions
	 */
	public static String[] getPlayerNames(FabricClientCommandSource source) {
		return source.getPlayerNames().stream().filter(playerName -> playerName.matches("[A-Za-z0-9_]+")).toArray(String[]::new);
	}
}

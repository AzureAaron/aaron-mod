package net.azureaaron.mod.util;

/**
 * Record which holds a player's name and uuid, similar to a {@link com.mojang.authlib.GameProfile GameProfile}.
 * This is used for commands that take in a player argument.
 * 
 * @author Aaron
 */
public record CommandPlayerData(String name, String uuid) {
}

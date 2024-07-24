package net.azureaaron.mod.commands;

import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

/**
 * Base class extended by all Skyblock commands that require the Hypixel API.
 */
public abstract non-sealed class SkyblockCommand implements Command {

	/**
	 * @param source The source
	 * @param profile The target player's currently selected Skyblock profile
	 * @param name The target player's name
	 * @param uuid The target player's uuid
	 */
	public abstract void print(FabricClientCommandSource source, JsonObject profile, String name, String uuid);
}

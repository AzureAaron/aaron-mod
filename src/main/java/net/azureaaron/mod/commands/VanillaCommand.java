package net.azureaaron.mod.commands;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

/*
 * Base class for all "vanilla" commands which take in a player's name and UUID.
 */
public abstract non-sealed class VanillaCommand implements Command {

	/**
	 * @param source The source
	 * @param name The target player's name
	 * @param uuid The target player's uuid
	 */
	public abstract void print(FabricClientCommandSource source, String name, String uuid);
}

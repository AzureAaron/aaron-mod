package net.azureaaron.mod.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;

/**
 * Interface inherited by nearly all commands for the purpose of registering them without exposing their implementation.
 */
public sealed interface Command permits SkyblockCommand, VanillaCommand {

	void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess);
}

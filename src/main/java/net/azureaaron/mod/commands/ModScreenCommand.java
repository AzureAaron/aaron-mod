package net.azureaaron.mod.commands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.screens.ModScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;

public class ModScreenCommand {

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(ModScreenCommand::register);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(literal("aaronsmod")
				.executes(context -> handleCommand(context.getSource()))
				.then(literal("config")
						.executes(context -> handleOpenConfig(context.getSource())))
				.then(literal("options")
						.executes(context -> handleOpenConfig(context.getSource()))));
		
		dispatcher.register(literal("aaronmod")
				.executes(context -> handleCommand(context.getSource()))
				.then(literal("config")
						.executes(context -> handleOpenConfig(context.getSource())))
				.then(literal("options")
						.executes(context -> handleOpenConfig(context.getSource()))));
	}

	private static int handleCommand(FabricClientCommandSource source) {
		MinecraftClient client = source.getClient();
		client.send(() -> client.setScreen(new ModScreen(null)));

		return Command.SINGLE_SUCCESS;
	}

	private static int handleOpenConfig(FabricClientCommandSource source) {
		MinecraftClient client = source.getClient();
		client.send(() -> client.setScreen(AaronModConfigManager.createGui(null)));

		return Command.SINGLE_SUCCESS;
	}
}

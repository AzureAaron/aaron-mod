package net.azureaaron.mod.commands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.features.ModScreen;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class ModScreenCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("aaronsmod")
				.executes(context -> handleCommand(context.getSource())));
		
		dispatcher.register(literal("aaronmod")
				.executes(context -> handleCommand(context.getSource())));
	}
	
	private static int handleCommand(FabricClientCommandSource context) {
		MinecraftClient client = context.getClient();
		client.send(() -> {
			client.setScreen(new ModScreen(null));
		});
		return Command.SINGLE_SUCCESS;
	}
}

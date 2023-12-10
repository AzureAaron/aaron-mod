package net.azureaaron.mod.commands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.Timer;
import java.util.TimerTask;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.screens.ModScreen;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class ModScreenCommand {
	private static final long ONE_TICK = 50L;
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
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
		
		client.send(() -> {
			client.setScreen(new ModScreen(null));
		});
				
		return Command.SINGLE_SUCCESS;
	}
	
	private static int handleOpenConfig(FabricClientCommandSource source) {
		MinecraftClient client = source.getClient();
		TimerTask timedTask = new TimerTask() {
			@Override
			public void run() {
				client.send(() -> {
					client.setScreen(AaronModConfigManager.createGui(null));
				});
			}
		};
		
		new Timer().schedule(timedTask, ONE_TICK);
		
		return Command.SINGLE_SUCCESS;
	}
}

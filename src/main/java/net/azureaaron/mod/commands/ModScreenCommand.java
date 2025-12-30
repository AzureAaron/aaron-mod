package net.azureaaron.mod.commands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.screens.ModScreen;
import net.azureaaron.mod.utils.render.hud.HudElementConfigScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

public class ModScreenCommand {

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(ModScreenCommand::register);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("aaronmod")
				.executes(context -> handleOpenModScreen(context.getSource()))
				.then(literal("config")
						.executes(context -> handleOpenConfig(context.getSource())))
				.then(literal("options")
						.executes(context -> handleOpenConfig(context.getSource())))
				.then(literal("hud")
						.executes(context -> handleOpenHudConfig(context.getSource()))));
	}

	private static int handleOpenModScreen(FabricClientCommandSource source) {
		Minecraft client = source.getClient();
		client.schedule(() -> client.setScreen(new ModScreen(null)));

		return Command.SINGLE_SUCCESS;
	}

	private static int handleOpenConfig(FabricClientCommandSource source) {
		Minecraft client = source.getClient();
		client.schedule(() -> client.setScreen(AaronModConfigManager.createGui(null)));

		return Command.SINGLE_SUCCESS;
	}

	private static int handleOpenHudConfig(FabricClientCommandSource source) {
		Minecraft client = source.getClient();
		client.schedule(() -> client.setScreen(new HudElementConfigScreen(null)));

		return Command.SINGLE_SUCCESS;
	}
}

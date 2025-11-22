package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.annotations.Init;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class TestCommand {
	private static final boolean ENABLED = Boolean.parseBoolean(System.getProperty("aaronmod.enableTestCommand", "false")) || FabricLoader.getInstance().isDevelopmentEnvironment();

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(TestCommand::register);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		if (ENABLED) {
			dispatcher.register(literal("test")
					.executes(context -> printTest(context.getSource()))
					.then(argument("option", word())
							.executes(context -> printTest(context.getSource(), getString(context, "option")))));
		}
	}

	private static int printTest(FabricClientCommandSource source) {
		source.sendFeedback(Texts.bracketedCopyable("test!"));
		return Command.SINGLE_SUCCESS;
	}

	private static int printTest(FabricClientCommandSource source, String option) {
		source.sendFeedback(Text.literal("No tests to be done right now!"));
		return Command.SINGLE_SUCCESS;
	}
}

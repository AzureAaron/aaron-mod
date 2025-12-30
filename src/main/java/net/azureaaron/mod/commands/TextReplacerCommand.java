package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.features.TextReplacer;
import net.azureaaron.mod.screens.TextReplacerConfigScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class TextReplacerCommand {

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(TextReplacerCommand::register);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("textreplacer")
				//.executes(context -> openTextReplacerConfig(context.getSource()))
				.then(literal("add")
						.then(argument("replacementText", string())
								.then(argument("textComponent", ClientTextArgumentType.text())
										.executes(context -> addReplacement(context.getSource(), getString(context, "replacementText"), context.getArgument("textComponent", Component.class))))))
				.then(literal("remove")
						.then(argument("replacementText", string())
								.suggests((commandSource, builder) -> SharedSuggestionProvider.suggest(TextReplacer.getTextReplacements(), builder))
								.executes(context -> removeReplacement(context.getSource(), getString(context, "replacementText"))))));
	}

	@SuppressWarnings("unused")
	private static int openTextReplacerConfig(FabricClientCommandSource source) {
		Minecraft client = source.getClient();

		client.schedule(() -> client.setScreen(new TextReplacerConfigScreen(null)));

		return Command.SINGLE_SUCCESS;
	}

	private static int addReplacement(FabricClientCommandSource source, String replacementText, Component textComponent) {
		/*String tcText = textComponent.getString();

		if (tcText.length() != TextTransformer.deconstructAllComponents(textComponent).getSiblings().size()) {
			source.sendError(Text.literal("The text component contains unsupported characters!"));
			return Command.SINGLE_SUCCESS;
		}*/

		TextReplacer.addTextReplacement(replacementText, textComponent);
		source.sendFeedback(Component.literal("Successfully added the text replacement \"" + replacementText + "\""));

		return Command.SINGLE_SUCCESS;
	}

	private static int removeReplacement(FabricClientCommandSource source, String replacementText) {
		boolean success = TextReplacer.removeTextReplacement(replacementText);

		if (success) source.sendFeedback(Component.literal("Successfully removed the text replacement for \"" + replacementText + "\"")); else source.sendError(Component.literal("That text replacement never existed!"));

		return Command.SINGLE_SUCCESS;
	}
}

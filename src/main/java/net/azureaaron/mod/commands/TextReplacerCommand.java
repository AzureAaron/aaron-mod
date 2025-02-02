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
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class TextReplacerCommand {
	
	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(TextReplacerCommand::register);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(literal("textreplacer")
				//.executes(context -> openTextReplacerConfig(context.getSource()))
				.then(literal("add")
						.then(argument("replacementText", string())
								.then(argument("textComponent", ClientTextArgumentType.text())
										.executes(context -> addReplacement(context.getSource(), getString(context, "replacementText"), context.getArgument("textComponent", Text.class))))))
				.then(literal("remove")
						.then(argument("replacementText", string())
								.suggests((commandSource, builder) -> CommandSource.suggestMatching(TextReplacer.getTextReplacements(), builder))
								.executes(context -> removeReplacement(context.getSource(), getString(context, "replacementText"))))));
	}

	@SuppressWarnings("unused")
	private static int openTextReplacerConfig(FabricClientCommandSource source) {
		MinecraftClient client = source.getClient();

		client.send(() -> client.setScreen(new TextReplacerConfigScreen(null)));

		return Command.SINGLE_SUCCESS;
	}

	private static int addReplacement(FabricClientCommandSource source, String replacementText, Text textComponent) {
		/*String tcText = textComponent.getString();

		if (tcText.length() != TextTransformer.deconstructAllComponents(textComponent).getSiblings().size()) {
			source.sendError(Text.literal("The text component contains unsupported characters!"));
			return Command.SINGLE_SUCCESS;
		}*/

		TextReplacer.addTextReplacement(replacementText, textComponent);
		source.sendFeedback(Text.literal("Successfully added the text replacement \"" + replacementText + "\""));

		return Command.SINGLE_SUCCESS;
	}
	
	private static int removeReplacement(FabricClientCommandSource source, String replacementText) {
		boolean success = TextReplacer.removeTextReplacement(replacementText);
		
		if (success) source.sendFeedback(Text.literal("Successfully removed the text replacement for \"" + replacementText + "\"")); else source.sendError(Text.literal("That text replacement never existed!"));
		
		return Command.SINGLE_SUCCESS;
	}
}

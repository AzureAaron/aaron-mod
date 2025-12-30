package net.azureaaron.mod.commands.vanilla;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.mixins.accessors.ChatComponentAccessor;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

public class CopyChatCommand {
	private static final Minecraft minecraftClient = Minecraft.getInstance();
	private static final Component successToastTitle = Component.literal("Success!");
	private static final Component successToastDescription = Component.literal("The message was copied to your clipboard!");
	private static final Component notFoundToastTitle = Component.literal("Not Found!");
	private static final Component notFoundToastDescription = Component.literal("No message contained your input!");

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(CopyChatCommand::register);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		LiteralCommandNode<FabricClientCommandSource> copyChatCommand = dispatcher.register(literal("copychat")
				.then(argument("excerpt", greedyString())
						.executes(context -> copyMessage(context.getSource(), getString(context, "excerpt")))));

		dispatcher.register(literal("copymessage").redirect(copyChatCommand));
	}

	/**
	 * The new middle click to copy chat supersedes this feature!
	 */
	private static int copyMessage(FabricClientCommandSource source, String excerpt) {
		List<GuiMessage> chatHistory = ((ChatComponentAccessor) minecraftClient.gui.getChat()).getMessages();
		int maxChatHistoryLength = ChatComponentAccessor.getMaxHistoryLength();
		int maxIteration = (chatHistory.size() >= maxChatHistoryLength) ? maxChatHistoryLength : chatHistory.size();
		boolean foundAMessage = false;

		for (int i = 0; i < maxIteration; i++) {
			String currentMessage = chatHistory.get(i).content().getString();
			currentMessage = ChatFormatting.stripFormatting(currentMessage);
			if (currentMessage.contains(excerpt)) {
				minecraftClient.keyboardHandler.setClipboard(currentMessage);
				SystemToast.add(minecraftClient.getToastManager(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION, successToastTitle, successToastDescription);
				foundAMessage = true;
				break;
			}
		}
		if (!foundAMessage) SystemToast.add(minecraftClient.getToastManager(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION, notFoundToastTitle, notFoundToastDescription);
		return Command.SINGLE_SUCCESS;
	}
}

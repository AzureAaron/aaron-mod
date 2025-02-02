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
import net.azureaaron.mod.mixins.accessors.ChatAccessor;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CopyChatCommand {
	private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
	private static final Text successToastTitle = Text.literal("Success!");
	private static final Text successToastDescription = Text.literal("The message was copied to your clipboard!");
	private static final Text notFoundToastTitle = Text.literal("Not Found!");
	private static final Text notFoundToastDescription = Text.literal("No message contained your input!");
	
	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(CopyChatCommand::register);
	}
	
	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		LiteralCommandNode<FabricClientCommandSource> copyChatCommand = dispatcher.register(literal("copychat")
				.then(argument("excerpt", greedyString())
						.executes(context -> copyMessage(context.getSource(), getString(context, "excerpt")))));
		
		dispatcher.register(literal("copymessage").redirect(copyChatCommand));
		dispatcher.register(literal("cc").redirect(copyChatCommand));
		dispatcher.register(literal("cm").redirect(copyChatCommand));
	}
	
	/**
	 * The new middle click to copy chat supersedes this feature!
	 */
    private static int copyMessage(FabricClientCommandSource source, String excerpt) {
    	List<ChatHudLine> chatHistory = ((ChatAccessor) minecraftClient.inGameHud.getChatHud()).getMessages();
    	int maxChatHistoryLength = ChatAccessor.getMaxHistoryLength();
    	int maxIteration = (chatHistory.size() >= maxChatHistoryLength) ? maxChatHistoryLength : chatHistory.size();
    	boolean foundAMessage = false;
    	
    	for(int i = 0; i < maxIteration; i++) {
    		String currentMessage = chatHistory.get(i).content().getString();
    		currentMessage = Formatting.strip(currentMessage);
    		if(currentMessage.contains(excerpt)) {
    			minecraftClient.keyboard.setClipboard(currentMessage);
    			SystemToast.add(minecraftClient.getToastManager(), SystemToast.Type.PERIODIC_NOTIFICATION, successToastTitle, successToastDescription);
    			foundAMessage = true;
    			break;
    		}
    	}
    	if(!foundAMessage) SystemToast.add(minecraftClient.getToastManager(), SystemToast.Type.PERIODIC_NOTIFICATION, notFoundToastTitle, notFoundToastDescription);
        return Command.SINGLE_SUCCESS;
    }
}

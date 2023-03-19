package net.azureaaron.mod.listeners;

import java.util.List;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.events.MouseEvent;
import net.azureaaron.mod.mixins.ChatAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

public class MouseListener {
	private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
	private static final Text successToastTitle = Text.literal("Success!");
	private static final Text successToastDescription = Text.literal("The message was copied to your clipboard!");
	private static final Text notFoundToastTitle = Text.literal("Not Found!");
	private static final Text notFoundToastDescription = Text.literal("No message was hovered over!");
	
	private static int getMessageIndex(double chatLineX, double chatLineY) {
		int lineIndex = ((ChatAccessor) minecraftClient.inGameHud.getChatHud()).getMessageLineIndex(chatLineX, chatLineY);
		if(lineIndex == -1) return -1;
		
		List<ChatHudLine> messages = ((ChatAccessor) minecraftClient.inGameHud.getChatHud()).getMessages();
		List<ChatHudLine.Visible> visibleMessages = ((ChatAccessor) minecraftClient.inGameHud.getChatHud()).getVisibleMessages();
		int upperbound = 0; //Upper-bound value of range (position of start top of entry)
		int lowerbound = ((ChatAccessor) minecraftClient.inGameHud.getChatHud()).getMessageEndLineIndex(chatLineX, chatLineY); //Lower-bound value of range (position of end of entry)
		
		for(int i = lowerbound + 1; i < visibleMessages.size(); i++) { //Iterate until we encounter the end of the next message
			if(visibleMessages.get(i).endOfEntry()) {
				upperbound = i - 1;
				break;
			}
		}
		
		StringBuilder hoveredMessage = new StringBuilder();
		
		for(int i = upperbound; i >= lowerbound; i--) { //Iterate over the entries apart of this message and build the messages content
			ChatHudLine.Visible currentEntry = visibleMessages.get(i);
			currentEntry.content().accept((index, style, codePoint) -> {
				hoveredMessage.append((char) codePoint);
				return true;
			});
		}
		
		for(int i = 0; i < messages.size(); i++) { //Iterate over all stored messages
			ChatHudLine currentMessage = messages.get(i);
			String messageContent = Formatting.strip(currentMessage.content().getString()).replaceAll("\n", "");
			if(messageContent.equals(hoveredMessage.toString())) return i;
		}
		
		return -1;
	}
	
	public static void listen() {
		MouseEvent.EVENT.register((button, action, mods) -> {
			//Button 0 = left click, Button 1 = right click, Button 2 = middle click & others are fancy mouse buttons
			boolean isChatOpen = ((ChatAccessor) minecraftClient.inGameHud.getChatHud()).isChatFocused();
			if(button == 2 && action == 1 && isChatOpen && Config.copyChatMessages) {
				int mouseX = (int)(minecraftClient.mouse.getX() * minecraftClient.getWindow().getScaledWidth() / minecraftClient.getWindow().getWidth());
				int mouseY = (int)(minecraftClient.mouse.getY() * minecraftClient.getWindow().getScaledHeight() / minecraftClient.getWindow().getHeight());
				double chatLineX = ((ChatAccessor) minecraftClient.inGameHud.getChatHud()).toChatLineX(mouseX);
				double chatLineY = ((ChatAccessor) minecraftClient.inGameHud.getChatHud()).toChatLineY(mouseY);
								
				if(Config.copyChatMode == Config.CopyChatMode.SINGLE_LINE) {
					int messageLineIndex = ((ChatAccessor) minecraftClient.inGameHud.getChatHud()).getMessageLineIndex(chatLineX, chatLineY);
					List<ChatHudLine.Visible> visibleMessages = ((ChatAccessor) minecraftClient.inGameHud.getChatHud()).getVisibleMessages();
					
					if (messageLineIndex >= 0 && messageLineIndex < visibleMessages.size()) {
						OrderedText orderedText = visibleMessages.get(messageLineIndex).content();
						StringBuilder message = new StringBuilder();
						
						orderedText.accept((index, style, codePoint) -> {
							message.append((char) codePoint);
							return true;
						});
						
						minecraftClient.keyboard.setClipboard(message.toString());
		    			SystemToast.add(minecraftClient.getToastManager(), SystemToast.Type.PERIODIC_NOTIFICATION, successToastTitle, successToastDescription);
					} else {
						SystemToast.add(minecraftClient.getToastManager(), SystemToast.Type.PERIODIC_NOTIFICATION, notFoundToastTitle, notFoundToastDescription);
					}
				}
				
				if(Config.copyChatMode == Config.CopyChatMode.ENTIRE_MESSAGE) {
					int messageIndex = getMessageIndex(chatLineX, chatLineY);
					List<ChatHudLine> messages = ((ChatAccessor) minecraftClient.inGameHud.getChatHud()).getMessages();
					
					if (messageIndex >= 0 && messageIndex < messages.size()) {
						String message = Formatting.strip(messages.get(messageIndex).content().getString());
						
						minecraftClient.keyboard.setClipboard(message);
		    			SystemToast.add(minecraftClient.getToastManager(), SystemToast.Type.PERIODIC_NOTIFICATION, successToastTitle, successToastDescription);
					} else {
						SystemToast.add(minecraftClient.getToastManager(), SystemToast.Type.PERIODIC_NOTIFICATION, notFoundToastTitle, notFoundToastDescription);
					}
				}
			}
			return ActionResult.SUCCESS;
		});
	}
}

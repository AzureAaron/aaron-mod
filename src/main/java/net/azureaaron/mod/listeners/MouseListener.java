package net.azureaaron.mod.listeners;

import java.util.List;

import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.MouseInputEvent;
import net.azureaaron.mod.mixins.accessors.ChatAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.Window;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MouseListener {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final Text SUCCESS_TITLE = Text.literal("Success!");
	private static final Text SUCCESS_DESCRIPTION = Text.literal("The message was copied to your clipboard!");
	private static final Text NOT_FOUND_TITLE = Text.literal("Not Found!");
	private static final Text NOT_FOUND_DESCRIPTION = Text.literal("No message was hovered over!");

	public static void init() {
		MouseInputEvent.EVENT.register(MouseListener::onMouseInput);
	}	

	private static void onMouseInput(int button, int action, int mods) {
		//Button 0 = left click, Button 1 = right click, Button 2 = middle click & others are fancy mouse buttons
		int configuredButton = AaronModConfigManager.get().copyChatMouseButton == AaronModConfig.MouseButton.MIDDLE ? 2 : 1;
		ChatAccessor chatAccessor = ((ChatAccessor) CLIENT.inGameHud.getChatHud());
		boolean isChatOpen = chatAccessor.invokeIsChatFocused();

		if (button == configuredButton && action == 1 && isChatOpen && AaronModConfigManager.get().copyChatMessages) {
			Window window = CLIENT.getWindow();
			int mouseX = (int) (CLIENT.mouse.getX() * window.getScaledWidth() / window.getWidth());
			int mouseY = (int) (CLIENT.mouse.getY() * window.getScaledHeight() / window.getHeight());
			double chatLineX = chatAccessor.invokeToChatLineX(mouseX);
			double chatLineY = chatAccessor.invokeToChatLineY(mouseY);

			switch (AaronModConfigManager.get().copyChatMode) {
				case SINGLE_LINE -> {
					int messageLineIndex = chatAccessor.invokeGetMessageLineIndex(chatLineX, chatLineY);
					List<ChatHudLine.Visible> visibleMessages = chatAccessor.getVisibleMessages();

					if (messageLineIndex >= 0 && messageLineIndex < visibleMessages.size()) {
						OrderedText orderedText = visibleMessages.get(messageLineIndex).content();
						StringBuilder message = new StringBuilder();

						orderedText.accept((index, style, codePoint) -> {
							message.appendCodePoint(codePoint);
							return true;
						});

						CLIENT.keyboard.setClipboard(message.toString());
						sendToast(true);
					} else {
						sendToast(false);
					}
				}

				case ENTIRE_MESSAGE -> {
					int messageIndex = getMessageIndex(chatLineX, chatLineY);
					List<ChatHudLine> messages = chatAccessor.getMessages();

					if (messageIndex >= 0 && messageIndex < messages.size()) {
						String message = Formatting.strip(messages.get(messageIndex).content().getString());

						CLIENT.keyboard.setClipboard(message);
						sendToast(true);
					} else {
						sendToast(false);
					}
				}
			}
		}
	}

	private static void sendToast(boolean success) {
		ToastManager toastManager = CLIENT.getToastManager();

		if (success) {
			SystemToast.add(toastManager, SystemToast.Type.PERIODIC_NOTIFICATION, SUCCESS_TITLE, SUCCESS_DESCRIPTION);
		} else {
			SystemToast.add(toastManager, SystemToast.Type.PERIODIC_NOTIFICATION, NOT_FOUND_TITLE, NOT_FOUND_DESCRIPTION);

		}
	}

	private static int getMessageIndex(double chatLineX, double chatLineY) {
		ChatAccessor chatAccessor = ((ChatAccessor) CLIENT.inGameHud.getChatHud());

		int lineIndex = chatAccessor.invokeGetMessageLineIndex(chatLineX, chatLineY);
		if (lineIndex == -1) return -1;

		List<ChatHudLine> messages = chatAccessor.getMessages();
		List<ChatHudLine.Visible> visibleMessages = chatAccessor.getVisibleMessages();
		int upperbound = 0; //Upper-bound value of range (position of start top of entry)
		int lowerbound = chatAccessor.invokeGetMessageEndLineIndex(chatLineX, chatLineY); //Lower-bound value of range (position of end of entry)

		for (int i = lowerbound + 1; i < visibleMessages.size(); i++) { //Iterate until we encounter the end of the next message
			if (visibleMessages.get(i).endOfEntry()) {
				upperbound = i - 1;
				break;
			}

			if (i == visibleMessages.size() - 1) upperbound = i; //If another entry end wasn't found
		}

		StringBuilder hoveredMessage = new StringBuilder();

		for (int i = upperbound; i >= lowerbound; i--) { //Iterate over the entries apart of this message and build the messages content
			ChatHudLine.Visible currentEntry = visibleMessages.get(i);
			currentEntry.content().accept((index, style, codePoint) -> {
				hoveredMessage.appendCodePoint(codePoint);
				return true;
			});
		}

		for (int i = 0; i < messages.size(); i++) { //Iterate over all stored messages
			ChatHudLine currentMessage = messages.get(i);
			String messageContent = Formatting.strip(currentMessage.content().getString()).replaceAll("\n", "").replaceAll(" ", "");
			if (messageContent.equals(hoveredMessage.toString().replaceAll(" ", ""))) return i;
		}

		return -1;
	}
}

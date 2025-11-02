package net.azureaaron.mod.features;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.config.configs.RefinementsConfig;
import net.azureaaron.mod.mixins.accessors.ChatAccessor;
import net.azureaaron.mod.utils.ItemUtils;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;

public class CopyChatMessages {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final Text SUCCESS_TITLE = Text.literal("Success!");
	private static final Text SUCCESS_DESCRIPTION = Text.literal("The message was copied to your clipboard!");
	private static final Text NOT_FOUND_TITLE = Text.literal("Not Found!");
	private static final Text NOT_FOUND_DESCRIPTION = Text.literal("No message was hovered over!");

	@Init
	public static void init() {
		ScreenEvents.AFTER_INIT.register((_client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof ChatScreen chat) {
				ScreenMouseEvents.afterMouseClick(chat).register(CopyChatMessages::onMouseInput);
			}
		});
	}

	private static boolean onMouseInput(Screen screen, Click click, boolean consumed) {
		int configuredButton = AaronModConfigManager.get().refinements.chat.copyChatMouseButton == RefinementsConfig.MouseButton.MIDDLE ? GLFW.GLFW_MOUSE_BUTTON_MIDDLE : GLFW.GLFW_MOUSE_BUTTON_LEFT;
		ChatAccessor chatAccessor = ((ChatAccessor) CLIENT.inGameHud.getChatHud());

		if (click.button() == configuredButton && AaronModConfigManager.get().refinements.chat.copyChatMessages) {
			double chatLineX = chatAccessor.invokeToChatLineX(click.x());
			double chatLineY = chatAccessor.invokeToChatLineY(click.y());

			switch (AaronModConfigManager.get().refinements.chat.copyChatMode) {
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

						return true;
					} else {
						sendToast(false);
					}
				}

				case ENTIRE_MESSAGE -> {
					int messageIndex = getMessageIndex(chatLineX, chatLineY);
					List<ChatHudLine> messages = chatAccessor.getMessages();

					if (messageIndex > -1 && messageIndex < messages.size()) {
						Text message = messages.get(messageIndex).content();
						String text2Copy = !click.hasAlt() ? Formatting.strip(message.getString()) : TextCodecs.CODEC.encodeStart(ItemUtils.getRegistryLookup().getOps(JsonOps.INSTANCE), message)
								.mapOrElse(JsonElement::toString, e -> "Error while encoding JSON text: " + e.message());

						CLIENT.keyboard.setClipboard(text2Copy);
						sendToast(true);

						return true;
					} else {
						sendToast(false);
					}
				}
			}
		}

		return false;
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
				if (!Character.isWhitespace(codePoint)) hoveredMessage.appendCodePoint(codePoint);

				return true;
			});
		}

		for (int i = 0; i < messages.size(); i++) { //Iterate over all stored messages
			ChatHudLine currentMessage = messages.get(i);
			String messageContent = StringUtils.deleteWhitespace(Formatting.strip(currentMessage.content().getString()));

			if (messageContent.equals(hoveredMessage.toString())) return i;
		}
		return -1;
	}
}

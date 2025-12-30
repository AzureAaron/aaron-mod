package net.azureaaron.mod.features;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.config.configs.RefinementsConfig;
import net.azureaaron.mod.mixins.accessors.ChatComponentAccessor;
import net.azureaaron.mod.utils.ItemUtils;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class CopyChatMessages {
	private static final Minecraft CLIENT = Minecraft.getInstance();
	private static final Component SUCCESS_TITLE = Component.literal("Success!");
	private static final Component SUCCESS_DESCRIPTION = Component.literal("The message was copied to your clipboard!");
	private static final Component NOT_FOUND_TITLE = Component.literal("Not Found!");
	private static final Component NOT_FOUND_DESCRIPTION = Component.literal("No message was hovered over!");

	@Init
	public static void init() {
		ScreenEvents.AFTER_INIT.register((_client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof ChatScreen chat) {
				ScreenMouseEvents.afterMouseClick(chat).register(CopyChatMessages::onMouseInput);
			}
		});
	}

	private static boolean onMouseInput(Screen screen, MouseButtonEvent click, boolean consumed) {
		int configuredButton = AaronModConfigManager.get().refinements.chat.copyChatMouseButton == RefinementsConfig.MouseButton.MIDDLE ? GLFW.GLFW_MOUSE_BUTTON_MIDDLE : GLFW.GLFW_MOUSE_BUTTON_LEFT;
		ChatComponentAccessor chatAccessor = ((ChatComponentAccessor) CLIENT.gui.getChat());

		if (click.button() == configuredButton && AaronModConfigManager.get().refinements.chat.copyChatMessages) {
			double chatLineX = toChatLineX(click.x());
			double chatLineY = toChatLineY(click.y());

			switch (AaronModConfigManager.get().refinements.chat.copyChatMode) {
				case SINGLE_LINE -> {
					int messageLineIndex = getMessageLineIndex(chatLineX, chatLineY);
					List<GuiMessage.Line> visibleMessages = chatAccessor.getVisibleMessages();

					if (messageLineIndex >= 0 && messageLineIndex < visibleMessages.size()) {
						FormattedCharSequence orderedText = visibleMessages.get(messageLineIndex).content();
						StringBuilder message = new StringBuilder();

						orderedText.accept((index, style, codePoint) -> {
							message.appendCodePoint(codePoint);
							return true;
						});

						CLIENT.keyboardHandler.setClipboard(message.toString());
						sendToast(true);

						return true;
					} else {
						sendToast(false);
					}
				}

				case ENTIRE_MESSAGE -> {
					int messageIndex = getMessageIndex(chatLineX, chatLineY);
					List<GuiMessage> messages = chatAccessor.getMessages();

					if (messageIndex > -1 && messageIndex < messages.size()) {
						Component message = messages.get(messageIndex).content();
						String text2Copy = !click.hasAltDown() ? ChatFormatting.stripFormatting(message.getString()) : ComponentSerialization.CODEC.encodeStart(ItemUtils.getRegistryLookup().createSerializationContext(JsonOps.INSTANCE), message)
								.mapOrElse(JsonElement::toString, e -> "Error while encoding JSON text: " + e.message());

						CLIENT.keyboardHandler.setClipboard(text2Copy);
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
			SystemToast.add(toastManager, SystemToast.SystemToastId.PERIODIC_NOTIFICATION, SUCCESS_TITLE, SUCCESS_DESCRIPTION);
		} else {
			SystemToast.add(toastManager, SystemToast.SystemToastId.PERIODIC_NOTIFICATION, NOT_FOUND_TITLE, NOT_FOUND_DESCRIPTION);

		}
	}

	private static int getMessageIndex(double chatLineX, double chatLineY) {
		ChatComponentAccessor chatAccessor = ((ChatComponentAccessor) CLIENT.gui.getChat());

		int lineIndex = getMessageLineIndex(chatLineX, chatLineY);
		if (lineIndex == -1) return -1;

		List<GuiMessage> messages = chatAccessor.getMessages();
		List<GuiMessage.Line> visibleMessages = chatAccessor.getVisibleMessages();
		int upperbound = 0; //Upper-bound value of range (position of start top of entry)
		int lowerbound = getMessageEndLineIndex(chatLineX, chatLineY); //Lower-bound value of range (position of end of entry)

		for (int i = lowerbound + 1; i < visibleMessages.size(); i++) { //Iterate until we encounter the end of the next message
			if (visibleMessages.get(i).endOfEntry()) {
				upperbound = i - 1;
				break;
			}

			if (i == visibleMessages.size() - 1) upperbound = i; //If another entry end wasn't found
		}

		StringBuilder hoveredMessage = new StringBuilder();

		for (int i = upperbound; i >= lowerbound; i--) { //Iterate over the entries apart of this message and build the messages content
			GuiMessage.Line currentEntry = visibleMessages.get(i);

			currentEntry.content().accept((index, style, codePoint) -> {
				if (!Character.isWhitespace(codePoint)) hoveredMessage.appendCodePoint(codePoint);

				return true;
			});
		}

		for (int i = 0; i < messages.size(); i++) { //Iterate over all stored messages
			GuiMessage currentMessage = messages.get(i);
			String messageContent = StringUtils.deleteWhitespace(ChatFormatting.stripFormatting(currentMessage.content().getString()));

			if (messageContent.equals(hoveredMessage.toString())) return i;
		}
		return -1;
	}

	private static double toChatLineX(double x) {
		ChatComponentAccessor chatAccessor = ((ChatComponentAccessor) CLIENT.gui.getChat());
		return x / chatAccessor.invokeGetScale() - 4.0;
	}

	private static double toChatLineY(double y) {
		ChatComponentAccessor chatAccessor = ((ChatComponentAccessor) CLIENT.gui.getChat());
		double d = CLIENT.getWindow().getGuiScaledHeight() - y - 40.0;

		return d / (chatAccessor.invokeGetScale() * chatAccessor.invokeGetLineHeight());
	}

	private static int getMessageEndLineIndex(double chatLineX, double chatLineY) {
		ChatComponentAccessor chatAccessor = ((ChatComponentAccessor) CLIENT.gui.getChat());
		int i = getMessageLineIndex(chatLineX, chatLineY);
		if (i == -1) {
			return -1;
		} else {
			while (i >= 0) {
				if ((chatAccessor.getVisibleMessages().get(i)).endOfEntry()) {
					return i;
				}

				i--;
			}

			return i;
		}
	}

	private static int getMessageLineIndex(double chatLineX, double chatLineY) {
		ChatComponent chatHud = CLIENT.gui.getChat();
		ChatComponentAccessor chatAccessor = (ChatComponentAccessor) chatHud;

		if (chatHud.isChatFocused() && !chatAccessor.invokeIsChatHidden()) {
			if (!(chatLineX < -4.0) && !(chatLineX > Mth.floor(chatAccessor.invokeGetWidth() / chatAccessor.invokeGetScale()))) {
				int i = Math.min(chatHud.getLinesPerPage(), chatAccessor.getVisibleMessages().size());
				if (chatLineY >= 0.0 && chatLineY < i) {
					int j = Mth.floor(chatLineY + chatAccessor.getChatScrollbarPos());
					if (j >= 0 && j < chatAccessor.getVisibleMessages().size()) {
						return j;
					}
				}

				return -1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
}

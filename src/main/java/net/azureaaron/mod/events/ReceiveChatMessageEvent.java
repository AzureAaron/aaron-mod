package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;

/**
 * This event is called when a system message is received and cancelled.<br><br>
 * This is a simple hook over {@link ClientReceiveMessageEvents}.
 * 
 * @author Aaron
 */
@FunctionalInterface
public interface ReceiveChatMessageEvent {
	Event<ReceiveChatMessageEvent> EVENT = EventFactory.createArrayBacked(ReceiveChatMessageEvent.class,
			listeners -> (message, overlay, cancelled) -> {
				for (ReceiveChatMessageEvent listener : listeners) {
					listener.onMessage(message, overlay, cancelled);
				}
			});
	
	/**
	 * @param message The message
	 * @param overlay Whether the message will to be displayed in the action bar or not
	 * @param cancelled Whether the message was cancelled or not
	 */
	void onMessage(Text message, boolean overlay, boolean cancelled);
	
	static void init() {
		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			EVENT.invoker().onMessage(message, overlay, false);
		});
		
		ClientReceiveMessageEvents.GAME_CANCELED.register((message, overlay) -> {
			EVENT.invoker().onMessage(message, overlay, true);
		});
	}
}

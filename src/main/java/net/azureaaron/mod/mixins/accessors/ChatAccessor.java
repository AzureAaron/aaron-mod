package net.azureaaron.mod.mixins.accessors;

import java.util.List;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChatComponent.class)
public interface ChatAccessor {
	@Accessor("MAX_CHAT_HISTORY")
	static int getMaxHistoryLength() {
		throw new UnsupportedOperationException();
	}

	@Accessor("allMessages")
	List<GuiMessage> getMessages();

	@Accessor("trimmedMessages")
	List<GuiMessage.Line> getVisibleMessages();

	@Invoker
	double invokeGetScale();

	@Invoker
	int invokeGetLineHeight();

	@Invoker
	boolean invokeIsChatHidden();

	@Invoker
	int invokeGetWidth();

	@Accessor
	int getChatScrollbarPos();
}

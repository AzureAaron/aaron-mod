package net.azureaaron.mod.mixins.accessors;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;

@Mixin(ChatHud.class)
public interface ChatAccessor {
	@Accessor("MAX_MESSAGES")
	static int getMaxHistoryLength() {
		throw new UnsupportedOperationException();
	}

	@Accessor("messages")
	List<ChatHudLine> getMessages();

	@Accessor("visibleMessages")
	List<ChatHudLine.Visible> getVisibleMessages();

	@Invoker
	double invokeGetChatScale();

	@Invoker
	int invokeGetLineHeight();

	@Invoker
	boolean invokeIsChatHidden();

	@Invoker
	int invokeGetWidth();

	@Accessor
	int getScrolledLines();
}

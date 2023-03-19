package net.azureaaron.mod.mixins;

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
		return 0;
	};
	
    @Accessor("messages")
    List<ChatHudLine> getMessages();
    
    @Accessor("visibleMessages")
    List<ChatHudLine.Visible> getVisibleMessages();
    
    @Invoker("isChatFocused")
    boolean isChatFocused();
    
    @Invoker("toChatLineX")
    double toChatLineX(double x);
    
    @Invoker("toChatLineY")
    double toChatLineY(double y);
    
    @Invoker("getMessageLineIndex")
    int getMessageLineIndex(double chatLineX, double chatLineY);
        
    @Invoker("getMessageIndex")
    int getMessageEndLineIndex(double chatLineX, double chatLineY);
}

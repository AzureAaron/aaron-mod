package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import dev.cbyrne.betterinject.annotations.Arg;
import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.Config;
import net.azureaaron.mod.events.ReceiveChatMessageEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;

@Mixin(ChatHud.class)
public class ChatHudMixin {
	
	@Shadow @Final @Mutable private static int MAX_MESSAGES;
	
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"))
    private void aaronMod$addMessage(@Arg Text message) {
    	ReceiveChatMessageEvent.EVENT.invoker().onMessage(message, message.getString());
    }
    
    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", constant = @Constant(intValue = 100))
    private int aaronMod$longerChatHistory(int maxMessages) {
    	MAX_MESSAGES = (Config.chatHistoryLength < 100) ? 100 : Config.chatHistoryLength;
    	return (Config.chatHistoryLength < 100) ? 100 : Config.chatHistoryLength;
    }
}
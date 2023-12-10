package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.ImagePreview;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;

@Mixin(ChatHud.class)
public class ChatHudMixin {
	
	@Shadow @Final @Mutable private static int MAX_MESSAGES;
	@Shadow @Final private MinecraftClient client;
    
    @ModifyExpressionValue(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "CONSTANT", args = "intValue=100"))
    private int aaronMod$longerChatHistory(int maxMessages) {
    	MAX_MESSAGES = Math.max(100, AaronModConfigManager.get().chatHistoryLength);
    	return Math.max(100, AaronModConfigManager.get().chatHistoryLength);
    }
    
    @Inject(method = "clear", at = @At("TAIL"))
    private void aaronMod$emptyImagePreviewCacheAndFreeMemOnChatClear() {
    	ImagePreview.clearCache(client);
    }
}
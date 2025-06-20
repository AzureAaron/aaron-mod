package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.ImagePreview;
import net.azureaaron.mod.screens.itemmodel.CustomizeItemModelScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;

@Mixin(ChatHud.class)
public class ChatHudMixin {
	@Shadow
	@Mutable
	private static int MAX_MESSAGES;

	@Shadow
	@Final
	private MinecraftClient client;

	@ModifyExpressionValue(method = { "addVisibleMessage", "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", "addToMessageHistory" }, at = @At(value = "CONSTANT", args = "intValue=100"), require = 3)
	private int aaronMod$longerChatHistory(int maxMessages) {
		int historyLength = Math.max(Math.max(maxMessages, AaronModConfigManager.get().refinements.chat.chatHistoryLength), 100);

		return historyLength;
	}

	@Inject(method = "clear", at = @At("TAIL"))
	private void aaronMod$emptyImagePreviewCacheAndFreeMemOnChatClear(CallbackInfo ci) {
		ImagePreview.clearCache(this.client);
	}

	@ModifyReturnValue(method = "isChatHidden", at = @At("RETURN"))
	private boolean aaronMod$hideChatWhileCustomizingItemModel(boolean original) {
		return original || this.client.currentScreen instanceof CustomizeItemModelScreen;
	}
}
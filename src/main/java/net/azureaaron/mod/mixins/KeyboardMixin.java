package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.Config;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	//Substitute since we can't redirect LOOKUPSWITCH opcodes
	@ModifyVariable(method = "processF3", at = @At(value = "LOAD", ordinal = 0), argsOnly = true)
	private int aaronMod$fixF3PlusN(int original) {
		return Config.alternateF3PlusNKey && original == InputUtil.GLFW_KEY_J ? InputUtil.GLFW_KEY_N : original;
	}
	
	@Inject(method = "processF3", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V", ordinal = 6, shift = At.Shift.AFTER))
	private void aaronMod$addF3PlusJMessage() {
		if(Config.alternateF3PlusNKey) client.inGameHud.getChatHud().addMessage(Text.literal("F3 + J = Cycle previous gamemode <-> spectator"));
	}
	
	@Redirect(method = "processF3", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V", ordinal = 8))
	private void aaronMod$removeF3PlusNMessage(ChatHud chatHud, Text text) {
		if(!Config.alternateF3PlusNKey) chatHud.addMessage(text);
	}
}

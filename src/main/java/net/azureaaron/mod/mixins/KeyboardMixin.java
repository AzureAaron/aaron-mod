package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Mixin(Keyboard.class)
public class KeyboardMixin {

	//Substitute since we can't redirect LOOKUPSWITCH opcodes
	@ModifyVariable(method = "processF3", at = @At(value = "LOAD", ordinal = 0), argsOnly = true)
	private int aaronMod$fixF3PlusN(int original) {
		return AaronModConfigManager.get().refinements.input.alternateF3PlusNKeybind && original == InputUtil.GLFW_KEY_J ? InputUtil.GLFW_KEY_N : original;
	}

	@WrapOperation(method = "processF3", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
	private MutableText aaronMod$editF3PlusNMessageForNewKeybind(String translationKey, Operation<MutableText> operation) {
		if (AaronModConfigManager.get().refinements.input.alternateF3PlusNKeybind && translationKey.equals("debug.creative_spectator.help")) {
			return Text.literal("F3 + J = Cycle previous gamemode <-> spectator");
		} else {
			return operation.call(translationKey);
		}
	}
}

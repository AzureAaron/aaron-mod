package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.TextReplacer;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;

@Mixin(Font.class)
public class FontMixin {

	@ModifyVariable(method = "prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;", at = @At("HEAD"), argsOnly = true)
	private FormattedCharSequence aaronMod$visuallyReplaceOrderedText(FormattedCharSequence text) {
		return AaronModConfigManager.get().textReplacer.enableTextReplacer ? TextReplacer.visuallyReplaceText(text) : text;
	}
}

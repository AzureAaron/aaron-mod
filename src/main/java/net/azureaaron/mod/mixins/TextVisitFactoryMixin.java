package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;

@Mixin(TextVisitFactory.class)
public class TextVisitFactoryMixin {
	@Unique
	private static final Style CHROMA_BASE = Style.EMPTY.withColor(0xAA5500);

	@ModifyExpressionValue(method = "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", ordinal = 1, remap = false))
	private static char aaronMod$zFormattingCode(char code, @Local(ordinal = 2) LocalRef<Style> style) {
		Style currentStyle = style.get();

		if (Character.toLowerCase(code) == 'z') style.set(CHROMA_BASE.withClickEvent(currentStyle.getClickEvent()).withHoverEvent(currentStyle.getHoverEvent()).withInsertion(currentStyle.getInsertion()).withFont(currentStyle.getFont()));

		return code;
	}
}

package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.StringDecomposer;

@Mixin(StringDecomposer.class)
public class StringDecomposerMixin {
	@Unique
	private static final TextColor CHROMA = TextColor.fromRgb(0xAA5500);

	@ModifyExpressionValue(method = "iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", ordinal = 1))
	private static char aaronMod$zFormattingCode(char code, @Local(ordinal = 2) LocalRef<Style> style) {
		if (Character.toLowerCase(code) == 'z') {
			Style currentStyle = style.get();
			Style newStyle = new Style(CHROMA, currentStyle.getShadowColor(), false, false, false, false, false, currentStyle.getClickEvent(), currentStyle.getHoverEvent(), currentStyle.getInsertion(), currentStyle.getFont());

			style.set(newStyle);
		}

		return code;
	}
}

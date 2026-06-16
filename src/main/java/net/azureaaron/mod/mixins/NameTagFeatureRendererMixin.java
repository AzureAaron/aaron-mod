package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.TextReplacer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.util.FormattedCharSequence;

@Mixin(NameTagFeatureRenderer.class)
public class NameTagFeatureRendererMixin {

	@WrapOperation(method = "prepareText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;"))
	private static Font.PreparedText aaronMod$shadowedNametags(Font font, FormattedCharSequence text, float x, float y, int originalColour, boolean drawShadow, boolean includeEmpty, int backgroundColour, Operation<Font.PreparedText> operation) {
		drawShadow = AaronModConfigManager.get().uiAndVisuals.nameTags.shadowedNameTags;
		backgroundColour = (AaronModConfigManager.get().uiAndVisuals.nameTags.hideNameTagBackground) ? 0 : backgroundColour;

		if (AaronModConfigManager.get().textReplacer.enableTextReplacer) {
			x = -font.width(TextReplacer.visuallyReplaceText(text)) >> 1; // Fix x offset
		}

		return operation.call(font, text, x, y, originalColour, drawShadow, includeEmpty, backgroundColour);
	}
}

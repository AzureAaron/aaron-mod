package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.cbyrne.betterinject.annotations.Arg;
import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.Config;
import net.azureaaron.mod.features.FpsDisplay;
import net.azureaaron.mod.features.TextReplacer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    
    @WrapOperation(method = "method_55440", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"))
    private int aaronMod$shadowEntryTitleAndScoreText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int colour, boolean shadow, Operation<Integer> operation) {
    	return Config.shadowedScoreboard ? context.drawTextWithShadow(textRenderer, text, x, y, colour) : operation.call(context, textRenderer, text, x, y, colour, shadow);
    }
    
    @Inject(method = "render", at = @At("HEAD"))
    public void aaronMod$fpsDisplay(@Arg DrawContext context) {
    	if(Config.fpsDisplay) FpsDisplay.render(context);
    }
    
    @Redirect(method = "renderHeldItemTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"))
    private int aaronMod$correctXValue(TextRenderer textRenderer, StringVisitable text) {
    	return Config.visualTextReplacer ? textRenderer.getWidth(TextReplacer.visuallyReplaceText(((MutableText) text).asOrderedText())) : textRenderer.getWidth(text);
    }
}

package net.azureaaron.mod.mixins;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.TextReplacer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.scoreboard.number.BlankNumberFormat;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@ModifyVariable(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("STORE"))
	private NumberFormat aaronMod$hideScoreText(NumberFormat format) {
		return AaronModConfigManager.get().hideScoreboardScore ? BlankNumberFormat.INSTANCE : format;
	}

    @WrapOperation(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"))
    private int aaronMod$shadowEntryTitleAndScoreText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int colour, boolean shadow, Operation<Integer> operation) {
    	return AaronModConfigManager.get().shadowedScoreboard ? context.drawTextWithShadow(textRenderer, text, x, y, colour) : operation.call(context, textRenderer, text, x, y, colour, shadow);
    }

    @WrapOperation(method = "renderHeldItemTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"))
    private int aaronMod$correctXValue(TextRenderer textRenderer, StringVisitable text, Operation<Integer> operation) {
    	return AaronModConfigManager.get().visualTextReplacer ? textRenderer.getWidth(TextReplacer.visuallyReplaceText(((MutableText) text).asOrderedText())) : operation.call(textRenderer, text);
    }

    @Redirect(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"))
    private void aaronMod$statusEffectBackgroundAlpha(DrawContext context, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height) {
        context.drawGuiTexture(renderLayers, sprite, x, y, width, height, ColorHelper.withAlpha(ColorHelper.channelFromFloat(AaronModConfigManager.get().statusEffectBackgroundAlpha), 0xFFFFFF));
    }
}

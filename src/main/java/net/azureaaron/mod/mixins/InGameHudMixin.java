package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.TextReplacer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.number.BlankNumberFormat;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@ModifyVariable(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("STORE"))
	private NumberFormat aaronMod$hideScoreText(NumberFormat format) {
		return AaronModConfigManager.get().uiAndVisuals.scoreboard.hideScore ? BlankNumberFormat.INSTANCE : format;
	}

	@WrapOperation(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V"))
	private void aaronMod$shadowEntryTitleAndScoreText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int colour, boolean shadow, Operation<Integer> operation) {
		if (AaronModConfigManager.get().uiAndVisuals.scoreboard.shadowedScoreboardText) {
			context.drawTextWithShadow(textRenderer, text, x, y, colour);
		} else {
			operation.call(context, textRenderer, text, x, y, colour, shadow);
		}
	}

	@WrapOperation(method = "renderHeldItemTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"))
	private int aaronMod$correctXValue(TextRenderer textRenderer, StringVisitable text, Operation<Integer> operation) {
		return AaronModConfigManager.get().textReplacer.enableTextReplacer ? textRenderer.getWidth(TextReplacer.visuallyReplaceText(((MutableText) text).asOrderedText())) : operation.call(textRenderer, text);
	}

	@Redirect(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
	private void aaronMod$statusEffectBackgroundAlpha(DrawContext context, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
		context.drawGuiTexture(pipeline, sprite, x, y, width, height, AaronModConfigManager.get().uiAndVisuals.overlays.statusEffectBackgroundAlpha);
	}
}

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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.resources.Identifier;

@Mixin(Hud.class)
public class HudMixin {

	@ModifyVariable(method = "displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/scores/Objective;)V", at = @At("STORE"))
	private NumberFormat aaronMod$hideScoreText(NumberFormat format) {
		return AaronModConfigManager.get().uiAndVisuals.scoreboard.hideScore ? BlankFormat.INSTANCE : format;
	}

	@WrapOperation(method = "displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/scores/Objective;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"))
	private void aaronMod$shadowEntryTitleAndScoreText(GuiGraphicsExtractor graphics, Font textRenderer, Component text, int x, int y, int colour, boolean shadow, Operation<Integer> operation) {
		if (AaronModConfigManager.get().uiAndVisuals.scoreboard.shadowedScoreboardText) {
			graphics.text(textRenderer, text, x, y, colour);
		} else {
			operation.call(graphics, textRenderer, text, x, y, colour, shadow);
		}
	}

	@WrapOperation(method = "extractSelectedItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;width(Lnet/minecraft/network/chat/FormattedText;)I"))
	private int aaronMod$correctXValue(Font textRenderer, FormattedText text, Operation<Integer> operation) {
		return AaronModConfigManager.get().textReplacer.enableTextReplacer ? textRenderer.width(TextReplacer.visuallyReplaceText(((MutableComponent) text).getVisualOrderText())) : operation.call(textRenderer, text);
	}

	@Redirect(method = "extractEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
	private void aaronMod$statusEffectBackgroundAlpha(GuiGraphicsExtractor context, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
		context.blitSprite(pipeline, sprite, x, y, width, height, AaronModConfigManager.get().uiAndVisuals.overlays.statusEffectBackgroundAlpha);
	}
}

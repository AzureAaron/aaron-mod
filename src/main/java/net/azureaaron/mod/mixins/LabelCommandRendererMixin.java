package net.azureaaron.mod.mixins;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.TextReplacer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.network.chat.Component;

@Mixin(NameTagFeatureRenderer.class)
public class LabelCommandRendererMixin {

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"))
	private void aaronMod$shadowedNametags(Font textRenderer, Component text, float x, float y, int colour, boolean shadow, Matrix4f matrix, MultiBufferSource vertexConsumers, DisplayMode layerType, int backgroundColour, int light, Operation<Void> operation) {
		shadow = AaronModConfigManager.get().uiAndVisuals.nameTags.shadowedNameTags;
		backgroundColour = (AaronModConfigManager.get().uiAndVisuals.nameTags.hideNameTagBackground) ? 0 : backgroundColour;

		if (AaronModConfigManager.get().textReplacer.enableTextReplacer) {
			x = -textRenderer.width(TextReplacer.visuallyReplaceText(text.getVisualOrderText())) >> 1; // Fix x offset
		}

		operation.call(textRenderer, text, x, y, colour, shadow, matrix, vertexConsumers, layerType, backgroundColour, light);
	}
}

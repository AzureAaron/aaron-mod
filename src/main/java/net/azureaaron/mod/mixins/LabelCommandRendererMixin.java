package net.azureaaron.mod.mixins;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.TextReplacer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.LabelCommandRenderer;
import net.minecraft.text.Text;

@Mixin(LabelCommandRenderer.class)
public class LabelCommandRendererMixin {

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)V"))
	private void aaronMod$shadowedNametags(TextRenderer textRenderer, Text text, float x, float y, int colour, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColour, int light, Operation<Void> operation) {
		shadow = AaronModConfigManager.get().uiAndVisuals.nameTags.shadowedNameTags;
		backgroundColour = (AaronModConfigManager.get().uiAndVisuals.nameTags.hideNameTagBackground) ? 0 : backgroundColour;

		if (AaronModConfigManager.get().textReplacer.enableTextReplacer) {
			x = -textRenderer.getWidth(TextReplacer.visuallyReplaceText(text.asOrderedText())) >> 1; // Fix x offset
		}

		operation.call(textRenderer, text, x, y, colour, shadow, matrix, vertexConsumers, layerType, backgroundColour, light);
	}
}

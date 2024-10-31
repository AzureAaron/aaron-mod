package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.TextReplacer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

	@ModifyVariable(method = "drawInternal(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)I", at = @At("HEAD"), argsOnly = true)
	private OrderedText aaronMod$visuallyReplaceOrderedText(OrderedText text) {
		return AaronModConfigManager.get().visualTextReplacer ? TextReplacer.visuallyReplaceText(text) : text;
	}
}

package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.TextReplacer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

	@Inject(method = "drawInternal(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)I", at = @At("HEAD"))
	private void aaronMod$visuallyReplaceOrderedText(CallbackInfoReturnable<Integer> cir, @Local(argsOnly = true) OrderedText text, @Share("newText") LocalRef<OrderedText> newText) {
		if (AaronModConfigManager.get().visualTextReplacer) newText.set(TextReplacer.visuallyReplaceText(text));
	}

	@ModifyVariable(method = "drawInternal(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)I", at = @At("LOAD"))
	private OrderedText aaronMod$actuallyChangeTheText(OrderedText text, @Share("newText") LocalRef<OrderedText> newText) {
		return AaronModConfigManager.get().visualTextReplacer ? newText.get() : text;
	}
}

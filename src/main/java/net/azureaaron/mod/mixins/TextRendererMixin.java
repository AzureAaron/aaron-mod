package net.azureaaron.mod.mixins;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.TextReplacer;
import net.azureaaron.mod.utils.render.NametagDrawer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;

@Mixin(TextRenderer.class)
public class TextRendererMixin implements NametagDrawer {
	private static final Vector3f AARONMOD$BACKWARD_SHIFT = new Vector3f(0.0f, 0.0f, -0.03f);
	
	@Shadow 
	private static int tweakTransparency(int colour) {
		return 1;
	}
	
	@Shadow
	private float drawLayer(OrderedText text, float x, float y, int colour, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, TextLayerType layerType, int underlineColour, int light) {
		return 1f;
	}
	
	@Shadow 
	public int getWidth(OrderedText text) {
		return 1;
	}
	
	@Override
	public int drawNametag(OrderedText text, float x, float y, int colour, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, TextLayerType layerType, int backgroundColour, int light) {
		if (AaronModConfigManager.get().visualTextReplacer) {
			text = TextReplacer.visuallyReplaceText(text);
			x = -getWidth(text) / 2; //Fix x offset
		}

		colour = tweakTransparency(colour);
        Matrix4f matrix4f = new Matrix4f(matrix);
        if (shadow) {
            this.drawLayer(text, x, y, colour, true, matrix, vertexConsumerProvider, layerType, backgroundColour, light);
            matrix4f.translate(AARONMOD$BACKWARD_SHIFT);
        }
        x = this.drawLayer(text, x, y, colour, false, matrix4f, vertexConsumerProvider, layerType, backgroundColour, light);
        return (int) x + (shadow ? 1 : 0);
	}
	
	@Inject(method = "drawInternal(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", at = @At("HEAD"))
	private void aaronMod$visuallyReplaceOrderedText(CallbackInfoReturnable<Integer> cir, @Local(argsOnly = true) OrderedText text, @Share("newText") LocalRef<OrderedText> newText) {
		if (AaronModConfigManager.get().visualTextReplacer) newText.set(TextReplacer.visuallyReplaceText(text));
	}
	
	@ModifyVariable(method = "drawInternal(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", at = @At("LOAD"))
	private OrderedText aaronMod$actuallyChangeTheText(OrderedText text, @Share("newText") LocalRef<OrderedText> newText) {
		return AaronModConfigManager.get().visualTextReplacer ? newText.get() : text;
	}
}

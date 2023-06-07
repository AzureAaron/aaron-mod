package net.azureaaron.mod.mixins;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.azureaaron.mod.features.NametagDrawer;
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
	
	@Override
	public int drawNametag(OrderedText text, float x, float y, int colour, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, TextLayerType layerType, int backgroundColour, int light) {
		colour = tweakTransparency(colour);
        Matrix4f matrix4f = new Matrix4f(matrix);
        if (shadow) {
            this.drawLayer(text, x, y, colour, true, matrix, vertexConsumerProvider, layerType, backgroundColour, light);
            matrix4f.translate(AARONMOD$BACKWARD_SHIFT);
        }
        x = this.drawLayer(text, x, y, colour, false, matrix4f, vertexConsumerProvider, layerType, backgroundColour, light);
        return (int) x + (shadow ? 1 : 0);
	}
}

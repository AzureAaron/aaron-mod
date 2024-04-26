package net.azureaaron.mod.utils.render;

import org.joml.Matrix4f;

import net.azureaaron.mod.annotations.InterfaceInjected;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;

@InterfaceInjected
public interface NametagDrawer {
	int drawNametag(OrderedText text, float x, float y, int colour, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, TextLayerType layerType, int backgroundColour, int light);
}

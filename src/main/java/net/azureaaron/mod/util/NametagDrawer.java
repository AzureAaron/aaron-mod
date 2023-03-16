package net.azureaaron.mod.util;

import org.joml.Matrix4f;

import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;

@FunctionalInterface
public interface NametagDrawer {
	int drawNametag(OrderedText text, float x, float y, int colour, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, TextLayerType layerType, int backgroundColour, int light);
}

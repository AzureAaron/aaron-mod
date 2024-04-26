package net.azureaaron.mod.mixins;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.render.NametagDrawer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.Text;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"))
	private int aaronMod$shadowedNametags(TextRenderer textRenderer, Text text, float x, float y, int colour, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColour, int light) {
		backgroundColour = (AaronModConfigManager.get().hideNametagBackground) ? 0 : backgroundColour;
		return ((NametagDrawer) textRenderer).drawNametag(text.asOrderedText(), x, y, colour, AaronModConfigManager.get().shadowedNametags, matrix, vertexConsumers, layerType, backgroundColour, light);
	}
}

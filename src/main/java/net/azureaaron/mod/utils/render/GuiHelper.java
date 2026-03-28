package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;

import net.azureaaron.mod.mixins.accessors.GuiGraphicsExtractorInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.util.ARGB;

public class GuiHelper {
	private static final Minecraft MINECRAFT = Minecraft.getInstance();
	/**
	 * Suitable for rendering two blurred rectangles at once
	 */
	private static final TexturePool BLIT_TEXTURE_POOL = TexturePool.create("Blit Pool", 4, GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST, TextureFormat.RGBA8);
	private static int blitIndexForFrame = -1;

	/**
	 * Submits a blurred rectangle to be rendered at the given position.
	 *
	 * @param radius The strength of the blur, must be positive.
	 */
	public static void blurredRectangle(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int radius) {
		if (blitIndexForFrame == -1) {
			RenderTarget mainRenderTarget = MINECRAFT.getMainRenderTarget();
			int requiredWidth = mainRenderTarget.width;
			int requiredHeight = mainRenderTarget.height;
			blitIndexForFrame = BLIT_TEXTURE_POOL.getNextAvailableIndex(requiredWidth, requiredHeight);
		}

		GpuTextureView blitTextureView = BLIT_TEXTURE_POOL.getTextureView(blitIndexForFrame);
		// The sampler needs to be linear in order for the shader sampling interpolation trick to work properly
		GpuSampler sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
		// Pass the radius through the vertex colour - least painful way to do this
		int vertexColour = ARGB.color(radius, 255, 255);

		((GuiGraphicsExtractorInvoker) graphics).invokeInnerFill(AaronModRenderPipelines.BLURRED_RECTANGLE, TextureSetup.singleTexture(blitTextureView, sampler), x0, y0, x1, y1, vertexColour, null);
	}

	public static void updateScreenBlitTexture() {
		if (blitIndexForFrame != -1) {
			RenderTarget mainRenderTarget = MINECRAFT.getMainRenderTarget();
			int requiredWidth = mainRenderTarget.width;
			int requiredHeight = mainRenderTarget.height;
			GpuTextureView blitTextureView = BLIT_TEXTURE_POOL.getTextureView(blitIndexForFrame);

			// Copy the main render target colour texture to our temporary one since you cannot read from and write to the same texture in a single draw.
			RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(mainRenderTarget.getColorTexture(), blitTextureView.texture(), 0, 0, 0, 0, 0, requiredWidth, requiredHeight);
			blitIndexForFrame = -1;
		}
	}

	public static void close() {
		BLIT_TEXTURE_POOL.close();
	}
}

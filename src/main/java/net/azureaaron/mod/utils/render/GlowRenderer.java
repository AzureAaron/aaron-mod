package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.azureaaron.mod.mixins.accessors.OutlineBufferSourceAccessor;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.Util;

public class GlowRenderer implements AutoCloseable {
	private static GlowRenderer instance = null;
	private final Minecraft client;
	private final OutlineBufferSource glowOutlineVertexConsumers;
	private final TexturePool glowDepthTexturePool;
	private int currentPoolIndex = -1;
	private boolean isRenderingGlow = false;

	private GlowRenderer() {
		this.client = Minecraft.getInstance();
		this.glowOutlineVertexConsumers = Util.make(new OutlineBufferSource(), outlineVertexConsumers -> {
			((OutlineBufferSourceAccessor) outlineVertexConsumers).setOutlineBufferSource(new GlowVertexConsumerProvider(new ByteBufferBuilder(RenderType.TRANSIENT_BUFFER_SIZE)));
		});
		this.glowDepthTexturePool = TexturePool.create("Aaron Mod Glow Depth Tex", 2, GpuTexture.USAGE_RENDER_ATTACHMENT | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST, TextureFormat.DEPTH32);
	}

	public static GlowRenderer getInstance() {
		if (instance == null) {
			instance = new GlowRenderer();
		}

		return instance;
	}

	public OutlineBufferSource getGlowVertexConsumers() {
		return this.glowOutlineVertexConsumers;
	}

	public void updateGlowDepthTexDepth() {
		int requiredWidth = this.client.getWindow().getWidth();
		int requiredHeight = this.client.getWindow().getHeight();
		this.currentPoolIndex = this.glowDepthTexturePool.getNextAvailableIndex(requiredWidth, requiredHeight);

		RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(this.client.getMainRenderTarget().getDepthTexture(), this.glowDepthTexturePool.getTexture(this.currentPoolIndex), 0, 0, 0, 0, 0, requiredWidth, requiredHeight);
	}

	private void startRenderingGlow() {
		this.isRenderingGlow = true;
		RenderSystem.outputDepthTextureOverride = this.glowDepthTexturePool.getTextureView(this.currentPoolIndex);
	}

	private void stopRenderingGlow() {
		this.isRenderingGlow = false;
		RenderSystem.outputDepthTextureOverride = null;
	}

	public static boolean isRenderingGlow() {
		//Iris can load this class very early, so this is a static method that does not initialize the instance
		//to avoid crashing with it.
		return instance != null ? instance.isRenderingGlow : false;
	}

	@Override
	public void close() {
		this.glowDepthTexturePool.close();
	}

	private static class GlowVertexConsumerProvider extends MultiBufferSource.BufferSource {

		protected GlowVertexConsumerProvider(ByteBufferBuilder allocator) {
			super(allocator, Object2ObjectSortedMaps.emptyMap());
		}

		@Override
		public VertexConsumer getBuffer(RenderType renderLayer) {
			if (this.startedBuilders.get(renderLayer) != null && !renderLayer.canConsolidateConsecutiveGeometry()) {
				getInstance().startRenderingGlow();
				VertexConsumer buffer = super.getBuffer(renderLayer);
				getInstance().stopRenderingGlow();

				return buffer;
			}

			return super.getBuffer(renderLayer);
		}

		@Override
		public void endBatch(RenderType layer) {
			getInstance().startRenderingGlow();
			super.endBatch(layer);
			getInstance().stopRenderingGlow();
		}
	}
}

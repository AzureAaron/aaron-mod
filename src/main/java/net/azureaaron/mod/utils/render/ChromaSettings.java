package net.azureaaron.mod.utils.render;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.config.AaronModConfigManager;

public class ChromaSettings implements AutoCloseable {
	private static final int SIZE = new Std140SizeCalculator().putFloat().putFloat().putFloat().putFloat().get();
	protected final GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Chroma Settings", GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_UNIFORM, SIZE);

	protected void set(float ticks) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer buffer = Std140Builder.onStack(stack, SIZE)
					.putFloat(ticks)
					.putFloat(AaronModConfigManager.get().uiAndVisuals.chromaText.chromaSize)
					.putFloat(AaronModConfigManager.get().uiAndVisuals.chromaText.chromaSpeed)
					.putFloat(AaronModConfigManager.get().uiAndVisuals.chromaText.chromaSaturation)
					.get();
			RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), buffer);
		}
	}

	@Override
	public void close() {
		this.buffer.close();
	}
}

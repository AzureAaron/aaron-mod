package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.buffers.GpuBuffer;

import net.azureaaron.mod.utils.Scheduler;
import net.minecraft.client.render.RenderTickCounter;

public class ShaderUniforms {
	private static final ChromaSettings CHROMA_SETTINGS = new ChromaSettings();

	public static void updateShaderUniforms(RenderTickCounter tickCounter) {
		float ticks = Scheduler.INSTANCE.getCurrentTick() + tickCounter.getTickProgress(true);

		CHROMA_SETTINGS.set(ticks);
	}

	public static GpuBuffer getChromaUniform() {
		return CHROMA_SETTINGS.buffer;
	}

	public static void close() {
		CHROMA_SETTINGS.close();
	}
}

package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.buffers.GpuBuffer;

import net.azureaaron.mod.utils.Scheduler;
import net.minecraft.client.DeltaTracker;

public class ShaderUniforms {
	private static final ChromaSettingsUniform CHROMA_SETTINGS = new ChromaSettingsUniform();

	public static void updateShaderUniforms(DeltaTracker tickCounter) {
		float ticks = Scheduler.INSTANCE.getCurrentTick() + tickCounter.getGameTimeDeltaPartialTick(true);

		CHROMA_SETTINGS.set(ticks);
	}

	public static GpuBuffer getChromaUniform() {
		return CHROMA_SETTINGS.buffer;
	}

	public static void close() {
		CHROMA_SETTINGS.close();
	}
}

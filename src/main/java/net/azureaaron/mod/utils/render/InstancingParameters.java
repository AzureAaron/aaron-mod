package net.azureaaron.mod.utils.render;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.buffers.GpuBufferSlice;

public record InstancingParameters(int count, @Nullable String name, @Nullable GpuBufferSlice buffer) {
	public static final InstancingParameters NONE = new InstancingParameters(1, null, null);
}

package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.shaders.UniformType;

public class AaronModBindGroupLayouts {
	public static final BindGroupLayout CHROMA = BindGroupLayout.builder()
			.withUniform("Chroma", UniformType.UNIFORM_BUFFER)
			.build();
	public static final BindGroupLayout BOX_DATA = BindGroupLayout.builder()
			.withUniform("BoxData", UniformType.TEXEL_BUFFER, GpuFormat.RGBA32_FLOAT)
			.build();
	public static final BindGroupLayout OUTLINED_BOX_DATA = BindGroupLayout.builder()
			.withUniform("OutlinedBoxData", UniformType.TEXEL_BUFFER, GpuFormat.RGBA32_FLOAT)
			.build();
}

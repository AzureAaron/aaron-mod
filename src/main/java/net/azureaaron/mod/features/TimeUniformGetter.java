package net.azureaaron.mod.features;

import net.minecraft.client.gl.GlUniform;

@FunctionalInterface
public interface TimeUniformGetter {
	GlUniform getTime();
}

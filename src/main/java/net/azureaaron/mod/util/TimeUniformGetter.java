package net.azureaaron.mod.util;

import net.minecraft.client.gl.GlUniform;

@FunctionalInterface
public interface TimeUniformGetter {
	GlUniform getTime();
}

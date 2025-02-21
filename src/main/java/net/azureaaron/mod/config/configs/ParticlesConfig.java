package net.azureaaron.mod.config.configs;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.Identifier;

public class ParticlesConfig {
	@SerialEntry
	public Object2BooleanOpenHashMap<Identifier> states = new Object2BooleanOpenHashMap<>();

	@SerialEntry
	public Object2FloatOpenHashMap<Identifier> scaling = new Object2FloatOpenHashMap<>();

	@SerialEntry
	public Object2FloatOpenHashMap<Identifier> alphas = new Object2FloatOpenHashMap<>();
}

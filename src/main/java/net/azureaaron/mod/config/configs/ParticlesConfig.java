package net.azureaaron.mod.config.configs;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.Identifier;

public class ParticlesConfig {
	public Object2BooleanOpenHashMap<Identifier> states = new Object2BooleanOpenHashMap<>();

	public Object2FloatOpenHashMap<Identifier> scaling = new Object2FloatOpenHashMap<>();

	public Object2FloatOpenHashMap<Identifier> alphas = new Object2FloatOpenHashMap<>();
}

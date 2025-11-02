package net.azureaaron.mod.config.categories;

import net.azureaaron.dandelion.systems.ConfigCategory;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.Particles;
import net.azureaaron.mod.config.AaronModConfig;
import net.minecraft.text.Text;

public class ParticlesCategory {

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.id(Main.id("particles"))
				.name(Text.literal("Particles"))
				.groups(Particles.getOptionGroups(config))
				.build();
	}
}

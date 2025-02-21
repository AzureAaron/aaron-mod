package net.azureaaron.mod.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import net.azureaaron.mod.Particles;
import net.azureaaron.mod.config.AaronModConfig;
import net.minecraft.text.Text;

public class ParticlesCategory {

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.name(Text.literal("Particles"))
				.groups(Particles.getOptionGroups(config))
				.build();
	}
}

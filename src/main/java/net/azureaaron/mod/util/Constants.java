package net.azureaaron.mod.util;

import java.util.function.Supplier;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface Constants {
	Supplier<ColourProfiles> PROFILE = () -> AaronModConfigManager.get().colourProfile;
	Supplier<MutableText> PREFIX = () -> Text.empty()
			.append(Text.literal("[").formatted(Formatting.GRAY))
			.append(Text.literal("A").withColor(PROFILE.get().gradient(0.0)))
			.append(Text.literal("a").withColor(PROFILE.get().gradient(0.11)))
			.append(Text.literal("r").withColor(PROFILE.get().gradient(0.22)))
			.append(Text.literal("o").withColor(PROFILE.get().gradient(0.33)))
			.append(Text.literal("n").withColor(PROFILE.get().gradient(0.44)))
			.append(Text.literal("'").withColor(PROFILE.get().gradient(0.55)))
			.append(Text.literal("s").withColor(PROFILE.get().gradient(0.66)))
			.append(Text.literal(" "))
			.append(Text.literal("M").withColor(PROFILE.get().gradient(0.77)))
			.append(Text.literal("o").withColor(PROFILE.get().gradient(0.88)))
			.append(Text.literal("d").withColor(PROFILE.get().gradient(1.0)))
			.append(Text.literal("] ").formatted(Formatting.GRAY));
}

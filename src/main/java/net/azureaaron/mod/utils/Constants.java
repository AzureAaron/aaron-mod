package net.azureaaron.mod.utils;

import java.util.function.Supplier;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface Constants {
	Supplier<ColourProfiles> PROFILE = () -> AaronModConfigManager.get().general.colourProfile;
	Supplier<MutableComponent> PREFIX = () -> Component.empty()
			.append(Component.literal("[").withStyle(ChatFormatting.GRAY))
			.append(Component.literal("A").withColor(PROFILE.get().gradient(0.0)))
			.append(Component.literal("a").withColor(PROFILE.get().gradient(0.11)))
			.append(Component.literal("r").withColor(PROFILE.get().gradient(0.22)))
			.append(Component.literal("o").withColor(PROFILE.get().gradient(0.33)))
			.append(Component.literal("n").withColor(PROFILE.get().gradient(0.44)))
			.append(Component.literal("'").withColor(PROFILE.get().gradient(0.55)))
			.append(Component.literal("s").withColor(PROFILE.get().gradient(0.66)))
			.append(Component.literal(" "))
			.append(Component.literal("M").withColor(PROFILE.get().gradient(0.77)))
			.append(Component.literal("o").withColor(PROFILE.get().gradient(0.88)))
			.append(Component.literal("d").withColor(PROFILE.get().gradient(1.0)))
			.append(Component.literal("] ").withStyle(ChatFormatting.GRAY));
}

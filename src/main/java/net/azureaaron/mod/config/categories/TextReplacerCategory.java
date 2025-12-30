package net.azureaaron.mod.config.categories;

import net.azureaaron.dandelion.api.ButtonOption;
import net.azureaaron.dandelion.api.ConfigCategory;
import net.azureaaron.dandelion.api.Option;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.ConfigUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;

public class TextReplacerCategory {

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.id(Main.id("text_replacer"))
				.name(Component.literal("Text Replacer"))

				//Options
				.option(Option.<Boolean>createBuilder()
						.name(Component.literal("Enable Text Replacer"))
						.description(Component.literal("The text replacer allows you to visually replace almost any text on screen with whatever you want!")
								.append(Component.literal("\n\nSpecial: Use HEX #AA5500 or &z for "))
								.append(Component.literal("chroma text").withColor(0xAA5500))
								.append(Component.literal("!")))
						.binding(defaults.textReplacer.enableTextReplacer,
								() -> config.textReplacer.enableTextReplacer,
								newValue -> config.textReplacer.enableTextReplacer = newValue)
						.controller(ConfigUtils.createBooleanController())
						.build())
				.option(ButtonOption.createBuilder()
						.name(Component.literal("How to use this! (Hover)"))
						.prompt(Component.empty())
						.description(Component.literal("You can add text replacements with the command ")
								.append(Component.literal("/textreplacer add \"<textReplacement>\" <textComponent>").withStyle(ChatFormatting.GRAY))
								.append(Component.literal("\n\nYou're able to remove text replacements with the command "))
								.append(Component.literal("/textreplacer remove \"<textReplacement>\"").withStyle(ChatFormatting.GRAY))
								.append(Component.literal("\n\nIf you don't know how to create a text component use the website linked below, then copy n' paste the output!")))
						.action(action -> {}) //Do nothing I guess
						.build())
				.option(ButtonOption.createBuilder()
						.name(Component.literal("Text Component Generator Website"))
						.description(Component.literal("Click to open a link to the website!"))
						.prompt(Component.literal("\u29C9"))
						.action(screen -> ConfirmLinkScreen.confirmLinkNow(screen, "https://minecraft.tools/en/json_text.php"))
						.build())

				.build();
	}
}

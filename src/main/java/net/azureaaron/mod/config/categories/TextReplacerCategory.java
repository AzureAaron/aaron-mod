package net.azureaaron.mod.config.categories;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.ConfigUtils;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextReplacerCategory {

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.name(Text.literal("Text Replacer"))

				//Options
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Enable Text Replacer"))
						.description(OptionDescription.of(Text.literal("The text replacer allows you to visually replace almost any text on screen with whatever you want!")
								.append(Text.literal("\n\nSpecial: Use HEX #AA5500 or &z for "))
								.append(Text.literal("chroma text").withColor(0xAA5500))
								.append(Text.literal("!"))))
						.binding(defaults.textReplacer.enableTextReplacer,
								() -> config.textReplacer.enableTextReplacer,
								newValue -> config.textReplacer.enableTextReplacer = newValue)
						.controller(ConfigUtils::createBooleanController)
						.build())
				.option(ButtonOption.createBuilder()
						.name(Text.literal("How to use this! (Hover)"))
						.text(Text.empty())
						.description(OptionDescription.of(Text.literal("You can add text replacements with the command ")
								.append(Text.literal("/textreplacer add \"<textReplacement>\" <textComponent>").formatted(Formatting.GRAY))
								.append(Text.literal("\n\nYou're able to remove text replacements with the command "))
								.append(Text.literal("/textreplacer remove \"<textReplacement>\"").formatted(Formatting.GRAY))
								.append(Text.literal("\n\nIf you don't know how to create a text component use the website linked below, then copy n' paste the output!"))))
						.action((screen, opt) -> {}) //Do nothing I guess
						.build())
				.option(ButtonOption.createBuilder()
						.name(Text.literal("Text Component Generator Website"))
						.description(OptionDescription.of(Text.literal("Click to open a link to the website!")))
						.text(Text.literal("\u29C9"))
						.action((screen, opt) -> ConfirmLinkScreen.open(screen, "https://minecraft.tools/en/json_text.php"))
						.build())

				.build();
	}
}

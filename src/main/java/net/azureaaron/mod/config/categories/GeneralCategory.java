package net.azureaaron.mod.config.categories;

import java.awt.Color;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import net.azureaaron.mod.Colour;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.ConfigUtils;
import net.azureaaron.mod.screens.ModScreen;
import net.azureaaron.mod.utils.render.hud.HudElementConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class GeneralCategory {

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.name(Text.literal("General"))

				//Options
				.option(ButtonOption.createBuilder()
						.name(Text.literal("Aaron's Mod Main Screen"))
						.text(Text.literal("Open"))
						.action((screen, opt) -> MinecraftClient.getInstance().setScreen(new ModScreen(screen)))
						.build())
				.option(HudElementConfigScreen.createOption())
				.option(Option.<Colour.ColourProfiles>createBuilder()
						.name(Text.literal("Colour Profile"))
						.description(OptionDescription.of(Text.literal("Changes the colour of text used in commands!\n\nYou can choose from:\n")
								.append(Text.literal("Original\n").withColor(Colour.ColourProfiles.Original.primaryColour.getAsInt()))
								.append(Text.literal("Midnight\n").withColor(Colour.ColourProfiles.Midnight.primaryColour.getAsInt()))
								.append(Text.literal("Earth\n").withColor(Colour.ColourProfiles.Earth.primaryColour.getAsInt()))
								.append(Text.literal("Sakura\n").withColor(Colour.ColourProfiles.Sakura.primaryColour.getAsInt()))
								.append(Text.literal("Cloudy\n").withColor(Colour.ColourProfiles.Cloudy.primaryColour.getAsInt()))
								.append(Text.literal("Halloween\n").withColor(Colour.ColourProfiles.Halloween.primaryColour.getAsInt()))
								.append(Text.literal("Christmas\n").withColor(Colour.ColourProfiles.Christmas.primaryColour.getAsInt()))
								.append(Text.literal("Candyland\n").withColor(Colour.ColourProfiles.Candyland.primaryColour.getAsInt()))
								.append(Text.literal("Cyberpunk\n").withColor(Colour.ColourProfiles.Cyberpunk.primaryColour.getAsInt()))
								.append(Text.literal("Lava\n").withColor(Colour.ColourProfiles.Lava.primaryColour.getAsInt()))
								.append(Text.literal("and Ocean.").withColor(Colour.ColourProfiles.Ocean.primaryColour.getAsInt()))

								.append(Text.literal("\n\nOr you can make a "))
								.append(Text.literal("Custom").styled(style -> style.withItalic(true)))
								.append(Text.literal(" colour profile!"))))
						.binding(defaults.general.colourProfile,
								() -> config.general.colourProfile,
								newValue -> config.general.colourProfile = newValue)
						.controller(ConfigUtils::createEnumController)
						.build())

				//Custom Colour Profile
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Custom Colour Profile"))
						.description(OptionDescription.of(Text.literal("In order to use this you must set the Colour Profile option to Custom!")))
						.collapsed(true)
						.option(Option.<Color>createBuilder()
								.name(Text.literal("Primary Colour"))
								.binding(defaults.general.customColourProfile.primaryColour,
										() -> config.general.customColourProfile.primaryColour,
										newValue -> config.general.customColourProfile.primaryColour = newValue)
								.controller(ColorControllerBuilder::create)
								.build())
						.option(Option.<Color>createBuilder()
								.name(Text.literal("Secondary Colour"))
								.binding(defaults.general.customColourProfile.secondaryColour,
										() -> config.general.customColourProfile.secondaryColour,
										newValue -> config.general.customColourProfile.secondaryColour = newValue)
								.controller(ColorControllerBuilder::create)
								.build())
						.option(Option.<Color>createBuilder()
								.name(Text.literal("Info Colour"))
								.binding(defaults.general.customColourProfile.infoColour,
										() -> config.general.customColourProfile.infoColour,
										newValue -> config.general.customColourProfile.infoColour = newValue)
								.controller(ColorControllerBuilder::create)
								.build())
						.option(Option.<Color>createBuilder()
								.name(Text.literal("Highlight Colour"))
								.binding(defaults.general.customColourProfile.highlightColour,
										() -> config.general.customColourProfile.highlightColour,
										newValue -> config.general.customColourProfile.highlightColour = newValue)
								.controller(ColorControllerBuilder::create)
								.build())
						.option(Option.<Color>createBuilder()
								.name(Text.literal("Hover Colour"))
								.binding(defaults.general.customColourProfile.hoverColour,
										() -> config.general.customColourProfile.hoverColour,
										newValue -> config.general.customColourProfile.hoverColour = newValue)
								.controller(ColorControllerBuilder::create)
								.build())
						.option(Option.<Color>createBuilder()
								.name(Text.literal("Supporting Info Colour"))
								.binding(defaults.general.customColourProfile.supportingInfoColour,
										() -> config.general.customColourProfile.supportingInfoColour,
										newValue -> config.general.customColourProfile.supportingInfoColour = newValue)
								.controller(ColorControllerBuilder::create)
								.build())
						.build())

				.build();
	}
}

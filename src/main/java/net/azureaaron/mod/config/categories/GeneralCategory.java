package net.azureaaron.mod.config.categories;

import java.awt.Color;

import net.azureaaron.dandelion.api.ConfigType;
import net.azureaaron.dandelion.api.ButtonOption;
import net.azureaaron.dandelion.api.ConfigCategory;
import net.azureaaron.dandelion.api.Option;
import net.azureaaron.dandelion.api.OptionGroup;
import net.azureaaron.dandelion.api.controllers.ColourController;
import net.azureaaron.mod.Colour;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.ConfigUtils;
import net.azureaaron.mod.screens.ModScreen;
import net.azureaaron.mod.utils.render.hud.HudElementConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class GeneralCategory {

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.id(Main.id("general"))
				.name(Component.literal("General"))

				//Options
				.option(ButtonOption.createBuilder()
						.name(Component.literal("Aaron's Mod Main Screen"))
						.prompt(Component.literal("Open"))
						.action(screen -> Minecraft.getInstance().setScreen(new ModScreen(screen)))
						.build())
				.option(HudElementConfigScreen.createOption())
				.option(Option.<ConfigType>createBuilder()
						.name(Component.literal("Config Backend"))
						.description(Component.literal("You can choose between having YetAnotherConfigLib (YACL) or MoulConfig as the Config Backend."))
						.binding(defaults.general.configBackend,
								() -> config.general.configBackend,
								newValue -> config.general.configBackend = newValue)
						.controller(ConfigUtils.createEnumController())
						.build())
				.option(Option.<Colour.ColourProfiles>createBuilder()
						.name(Component.literal("Colour Profile"))
						.description(Component.literal("Changes the colour of text used in commands!\n\nYou can choose from:\n")
								.append(Component.literal("Original\n").withColor(Colour.ColourProfiles.Original.primaryColour.getAsInt()))
								.append(Component.literal("Midnight\n").withColor(Colour.ColourProfiles.Midnight.primaryColour.getAsInt()))
								.append(Component.literal("Earth\n").withColor(Colour.ColourProfiles.Earth.primaryColour.getAsInt()))
								.append(Component.literal("Sakura\n").withColor(Colour.ColourProfiles.Sakura.primaryColour.getAsInt()))
								.append(Component.literal("Cloudy\n").withColor(Colour.ColourProfiles.Cloudy.primaryColour.getAsInt()))
								.append(Component.literal("Halloween\n").withColor(Colour.ColourProfiles.Halloween.primaryColour.getAsInt()))
								.append(Component.literal("Christmas\n").withColor(Colour.ColourProfiles.Christmas.primaryColour.getAsInt()))
								.append(Component.literal("Candyland\n").withColor(Colour.ColourProfiles.Candyland.primaryColour.getAsInt()))
								.append(Component.literal("Cyberpunk\n").withColor(Colour.ColourProfiles.Cyberpunk.primaryColour.getAsInt()))
								.append(Component.literal("Lava\n").withColor(Colour.ColourProfiles.Lava.primaryColour.getAsInt()))
								.append(Component.literal("and Ocean.").withColor(Colour.ColourProfiles.Ocean.primaryColour.getAsInt()))

								.append(Component.literal("\n\nOr you can make a "))
								.append(Component.literal("Custom").withStyle(style -> style.withItalic(true)))
								.append(Component.literal(" colour profile!")))
						.binding(defaults.general.colourProfile,
								() -> config.general.colourProfile,
								newValue -> config.general.colourProfile = newValue)
						.controller(ConfigUtils.createEnumController())
						.build())

				//Custom Colour Profile
				.group(OptionGroup.createBuilder()
						.name(Component.literal("Custom Colour Profile"))
						.description(Component.literal("In order to use this you must set the Colour Profile option to Custom!"))
						.collapsed(true)
						.option(Option.<Color>createBuilder()
								.name(Component.literal("Primary Colour"))
								.binding(defaults.general.customColourProfile.primaryColour,
										() -> config.general.customColourProfile.primaryColour,
										newValue -> config.general.customColourProfile.primaryColour = newValue)
								.controller(ColourController.createBuilder().build())
								.build())
						.option(Option.<Color>createBuilder()
								.name(Component.literal("Secondary Colour"))
								.binding(defaults.general.customColourProfile.secondaryColour,
										() -> config.general.customColourProfile.secondaryColour,
										newValue -> config.general.customColourProfile.secondaryColour = newValue)
								.controller(ColourController.createBuilder().build())
								.build())
						.option(Option.<Color>createBuilder()
								.name(Component.literal("Info Colour"))
								.binding(defaults.general.customColourProfile.infoColour,
										() -> config.general.customColourProfile.infoColour,
										newValue -> config.general.customColourProfile.infoColour = newValue)
								.controller(ColourController.createBuilder().build())
								.build())
						.option(Option.<Color>createBuilder()
								.name(Component.literal("Highlight Colour"))
								.binding(defaults.general.customColourProfile.highlightColour,
										() -> config.general.customColourProfile.highlightColour,
										newValue -> config.general.customColourProfile.highlightColour = newValue)
								.controller(ColourController.createBuilder().build())
								.build())
						.option(Option.<Color>createBuilder()
								.name(Component.literal("Hover Colour"))
								.binding(defaults.general.customColourProfile.hoverColour,
										() -> config.general.customColourProfile.hoverColour,
										newValue -> config.general.customColourProfile.hoverColour = newValue)
								.controller(ColourController.createBuilder().build())
								.build())
						.option(Option.<Color>createBuilder()
								.name(Component.literal("Supporting Info Colour"))
								.binding(defaults.general.customColourProfile.supportingInfoColour,
										() -> config.general.customColourProfile.supportingInfoColour,
										newValue -> config.general.customColourProfile.supportingInfoColour = newValue)
								.controller(ColourController.createBuilder().build())
								.build())
						.build())

				.build();
	}
}

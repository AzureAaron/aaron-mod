package net.azureaaron.mod.config.categories;

import java.awt.Color;

import net.azureaaron.dandelion.api.ConfigCategory;
import net.azureaaron.dandelion.api.Option;
import net.azureaaron.dandelion.api.OptionGroup;
import net.azureaaron.dandelion.api.controllers.ColourController;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.ConfigUtils;
import net.azureaaron.mod.config.configs.SkyblockConfig;
import net.azureaaron.mod.utils.TextTransformer;
import net.minecraft.network.chat.Component;

public class SkyblockCategory {

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.id(Main.id("skyblock"))
				.name(Component.literal("Skyblock"))

				//Commands
				.group(OptionGroup.createBuilder()
						.name(Component.literal("Commands"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Enable Skyblock Commands"))
								.description(Component.literal("You can enable or disable the registration of the mod's skyblock commands."))
								.binding(defaults.skyblock.commands.enableSkyblockCommands,
										() -> config.skyblock.commands.enableSkyblockCommands,
										newValue -> config.skyblock.commands.enableSkyblockCommands = newValue)
								.controller(ConfigUtils.createBooleanController())
								//.flags(OptionFlag.GAME_RESTART)
								.build())
						.option(Option.<SkyblockConfig.DayAverage>createBuilder()
								.name(Component.literal("LBIN Price Day Average"))
								.description(Component.literal("Changes the day price average used in the /lbin command."))
								.binding(defaults.skyblock.commands.lbinPriceDayAverage,
										() -> config.skyblock.commands.lbinPriceDayAverage,
										newValue -> config.skyblock.commands.lbinPriceDayAverage = newValue)
								.controller(ConfigUtils.createEnumController())
								.build())
						.build())

				//Enchantments
				.group(OptionGroup.createBuilder()
						.name(Component.literal("Enchantments"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Rainbow Max Enchants"))
								.description(Component.literal("Changes the text colour of maximum level enchantments in an item's lore to be a pretty rainbow gradient!\n\nCheck out of the 'Rainbow Mode' option for examples and to change the way the gradient looks."))
								.binding(defaults.skyblock.enchantments.rainbowMaxEnchants,
										() -> config.skyblock.enchantments.rainbowMaxEnchants,
										newValue -> config.skyblock.enchantments.rainbowMaxEnchants = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<SkyblockConfig.RainbowMode>createBuilder()
								.name(Component.literal("Rainbow Mode"))
								.description(Component.literal("Changes how the rainbow gradient will look:\n")
										.append(Component.literal("\nStill: "))
										.append(TextTransformer.rainbowify("Critical VII, Vampirism VI"))
										.append(Component.literal("\nChroma: "))
										.append(Component.literal("Critical VII, Vampirism VI").withColor(0xAA5500)))
								.binding(defaults.skyblock.enchantments.rainbowMode,
										() -> config.skyblock.enchantments.rainbowMode,
										newValue -> config.skyblock.enchantments.rainbowMode = newValue)
								.controller(ConfigUtils.createEnumController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Show Good Enchants"))
								.description(Component.literal("Changes the text colour of enchantments that are at a \"good\" level.\n\nRequires the 'Rainbow Max Enchants' option to be enabled."))
								.binding(defaults.skyblock.enchantments.showGoodEnchants,
										() -> config.skyblock.enchantments.showGoodEnchants,
										newValue -> config.skyblock.enchantments.showGoodEnchants = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Color>createBuilder()
								.name(Component.literal("Good Enchants Colour"))
								.description(Component.literal("Changes the colour that \"good\" enchants will appear in."))
								.binding(defaults.skyblock.enchantments.goodEnchantsColour,
										() -> config.skyblock.enchantments.goodEnchantsColour,
										newValue -> config.skyblock.enchantments.goodEnchantsColour = newValue)
								.controller(ColourController.createBuilder().build())
								.build())
						.build())

				//Dungeons
				.group(OptionGroup.createBuilder()
						.name(Component.literal("Dungeons"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Party Finder Player Stats"))
								.description(Component.literal("Automatically looks up the stats of players who join your party through party finder."))
								.binding(defaults.skyblock.dungeons.dungeonFinderPlayerStats,
										() -> config.skyblock.dungeons.dungeonFinderPlayerStats,
										newValue -> config.skyblock.dungeons.dungeonFinderPlayerStats = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Old Master Stars"))
								.description(Component.literal("Brings back the old look and feel of master stars."))
								.binding(defaults.skyblock.dungeons.oldMasterStars,
										() -> config.skyblock.dungeons.oldMasterStars,
										newValue -> config.skyblock.dungeons.oldMasterStars = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Fancy Diamond Head Names"))
								.description(Component.literal("Makes the names of diamond heads a little fancier!"))
								.binding(defaults.skyblock.dungeons.fancyDiamondHeadNames,
										() -> config.skyblock.dungeons.fancyDiamondHeadNames,
										newValue -> config.skyblock.dungeons.fancyDiamondHeadNames = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Hide Tooltips In Click On Time"))
								.description(Component.literal("Hides tooltips in the Click On Time terminal so that they don't get in your way."))
								.binding(defaults.skyblock.dungeons.hideClickOnTimeTooltips,
										() -> config.skyblock.dungeons.hideClickOnTimeTooltips,
										newValue -> config.skyblock.dungeons.hideClickOnTimeTooltips = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.build())

				//M7
				.group(OptionGroup.createBuilder()
						.name(Component.literal("M7"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Glowing Dragons"))
								.description(Component.literal("Applies a glowing outline to the dragons, making them easier to spot.\n\nThe colour of the glow corresponds with the dragon's colour!"))
								.binding(defaults.skyblock.m7.glowingDragons,
										() -> config.skyblock.m7.glowingDragons,
										newValue -> config.skyblock.m7.glowingDragons = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Dragon Bounding Boxes"))
								.description(Component.literal("Draws bounding boxes around the dragon statues."))
								.binding(defaults.skyblock.m7.dragonBoundingBoxes,
										() -> config.skyblock.m7.dragonBoundingBoxes,
										newValue -> config.skyblock.m7.dragonBoundingBoxes = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Dragon Spawn Timers"))
								.description(Component.literal("Displays a timer under each statue that counts down to when the dragon spawns. The timer counts down in lockstep with the server's TPS."))
								.binding(defaults.skyblock.m7.dragonSpawnTimers,
										() -> config.skyblock.m7.dragonSpawnTimers,
										newValue -> config.skyblock.m7.dragonSpawnTimers = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Dragon Spawn Notifications"))
								.description(Component.literal("Displays a title notification and plays a sound to notify you that a dragon will spawn soon."))
								.binding(defaults.skyblock.m7.dragonSpawnNotifications,
										() -> config.skyblock.m7.dragonSpawnNotifications,
										newValue -> config.skyblock.m7.dragonSpawnNotifications = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Dragon Health Display"))
								.description(Component.literal("Displays the health of a dragon underneath it."))
								.binding(defaults.skyblock.m7.dragonHealthDisplay,
										() -> config.skyblock.m7.dragonHealthDisplay,
										newValue -> config.skyblock.m7.dragonHealthDisplay = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Dragon Aim Waypoints"))
								.description(Component.literal("Displays static aim waypoints to assist in shooting the dragon as it spawns."))
								.binding(defaults.skyblock.m7.dragonAimWaypoints,
										() -> config.skyblock.m7.dragonAimWaypoints,
										newValue -> config.skyblock.m7.dragonAimWaypoints = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Arrow Stack Waypoints"))
								.description(Component.literal("Displays waypoints to assist in finding a location to arrow stack from."))
								.binding(defaults.skyblock.m7.arrowStackWaypoints,
										() -> config.skyblock.m7.arrowStackWaypoints,
										newValue -> config.skyblock.m7.arrowStackWaypoints = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.build())

				.build();
	}
}

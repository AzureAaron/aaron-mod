package net.azureaaron.mod.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.ConfigUtils;
import net.azureaaron.mod.features.SeparateInventoryGuiScale;
import net.azureaaron.mod.utils.render.hud.HudElementConfigScreen;
import net.minecraft.text.Text;

public class UIAndVisualsCategory {

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.name(Text.literal("UI And Visuals"))

				//Scoreboard
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Scoreboard"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Shadowed Scoreboard Text"))
								.description(OptionDescription.of(Text.literal("Adds shadowing to all text on the scoreboard.")))
								.binding(defaults.uiAndVisuals.scoreboard.shadowedScoreboardText,
										() -> config.uiAndVisuals.scoreboard.shadowedScoreboardText,
										newValue -> config.uiAndVisuals.scoreboard.shadowedScoreboardText = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide Score"))
								.description(OptionDescription.of(Text.literal("Hides score numbers on the scoreboard.")))
								.binding(defaults.uiAndVisuals.scoreboard.hideScore,
										() -> config.uiAndVisuals.scoreboard.hideScore,
										newValue -> config.uiAndVisuals.scoreboard.hideScore = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.build())

				//Name Tags
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Name Tags"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Shadowed Name Tag Text"))
								.description(OptionDescription.of(Text.literal("Adds shadowing to name tag text.")))
								.binding(defaults.uiAndVisuals.nameTags.shadowedNameTags,
										() -> config.uiAndVisuals.nameTags.shadowedNameTags,
										newValue -> config.uiAndVisuals.nameTags.shadowedNameTags = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide Name Tag Background"))
								.description(OptionDescription.of(Text.literal("Hides the background behind name tags.")))
								.binding(defaults.uiAndVisuals.nameTags.hideNameTagBackground,
										() -> config.uiAndVisuals.nameTags.hideNameTagBackground,
										newValue -> config.uiAndVisuals.nameTags.hideNameTagBackground = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.build())

				//Overlays
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Overlays"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide Fire Overlay"))
								.description(OptionDescription.of(Text.literal("Hides the fire overlay, preventing it from taking up an excessive amount of screen space.")))
								.binding(defaults.uiAndVisuals.overlays.hideFireOverlay,
										() -> config.uiAndVisuals.overlays.hideFireOverlay,
										newValue -> config.uiAndVisuals.overlays.hideFireOverlay = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Status Effect Background Opacity"))
								.description(OptionDescription.of(Text.literal("Allows you to change the opacity of the background of status effects in the HUD.\n\nSet this to 0 if you want to hide the background.")))
								.binding(defaults.uiAndVisuals.overlays.statusEffectBackgroundAlpha,
										() -> config.uiAndVisuals.overlays.statusEffectBackgroundAlpha,
										newValue -> config.uiAndVisuals.overlays.statusEffectBackgroundAlpha = newValue)
								.controller(opt -> ConfigUtils.createFloatSliderController(opt, controller -> controller.range(0f, 1f).step(0.05f)))
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide Tutorials"))
								.description(OptionDescription.of(Text.literal("Hides those pesky tutorial toasts that think that you have no idea of what you're doing.")))
								.binding(defaults.uiAndVisuals.overlays.hideFireOverlay,
										() -> config.uiAndVisuals.overlays.hideFireOverlay,
										newValue -> config.uiAndVisuals.overlays.hideFireOverlay = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.build())

				//FPS HUD
				.group(OptionGroup.createBuilder()
						.name(Text.literal("FPS HUD"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Enable FPS Hud"))
								.binding(defaults.uiAndVisuals.fpsHud.enableFpsHud,
										() -> config.uiAndVisuals.fpsHud.enableFpsHud,
										newValue -> config.uiAndVisuals.fpsHud.enableFpsHud = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(HudElementConfigScreen.createOption())
						.build())

				//Ping HUD
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Ping HUD"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Enable Ping Hud"))
								.binding(defaults.uiAndVisuals.pingHud.enablePingHud,
										() -> config.uiAndVisuals.pingHud.enablePingHud,
										newValue -> config.uiAndVisuals.pingHud.enablePingHud = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Coloured Ping"))
								.description(OptionDescription.of(Text.literal("Adjusts the colour of the text depending on how much ping you have.")))
								.binding(defaults.uiAndVisuals.pingHud.colouredPing,
										() -> config.uiAndVisuals.pingHud.colouredPing,
										newValue -> config.uiAndVisuals.pingHud.colouredPing = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(HudElementConfigScreen.createOption())
						.build())

				//Debug HUD
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Debug HUD"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Extra Debug Info"))
								.description(OptionDescription.of(Text.literal("Displays some extra information about the system and process in the Debug HUD.")))
								.binding(defaults.uiAndVisuals.debugHud.extraDebugInfo,
										() -> config.uiAndVisuals.debugHud.extraDebugInfo,
										newValue -> config.uiAndVisuals.debugHud.extraDebugInfo = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Always Show Day In F3"))
								.description(OptionDescription.of(Text.literal("Due to an oversight in the fix of MC-230732, the world's day counter in the F3 menu is hidden outside of singleplayer; this option brings it back when outside of singleplayer.")))
								.binding(defaults.uiAndVisuals.debugHud.alwaysShowDayInF3,
										() -> config.uiAndVisuals.debugHud.alwaysShowDayInF3,
										newValue -> config.uiAndVisuals.debugHud.alwaysShowDayInF3 = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.build())

				//World
				.group(OptionGroup.createBuilder()
						.name(Text.literal("World"))
						.collapsed(true)
						.option(Option.<Double>createBuilder()
								.name(Text.literal("Zoom Multiplier"))
								.description(OptionDescription.of(Text.literal("Modifies how much your screen will zoom in by when zooming in.")))
								.binding(defaults.uiAndVisuals.world.zoomMultiplier,
										() -> config.uiAndVisuals.world.zoomMultiplier,
										newValue -> config.uiAndVisuals.world.zoomMultiplier = newValue)
								.controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.15d, 0.45d).step(0.01d))
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide World Loading Screen"))
								.description(OptionDescription.of(Text.literal("Hides the screen shown when loading into worlds.")))
								.binding(defaults.uiAndVisuals.world.hideWorldLoadingScreen,
										() -> config.uiAndVisuals.world.hideWorldLoadingScreen,
										newValue -> config.uiAndVisuals.world.hideWorldLoadingScreen = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide Mob Spawner Animations"))
								.description(OptionDescription.of(Text.literal("Hides the spinning mob and particles produced by mob spawners. Can make a huge impact on FPS in areas with lots of mob spawners.")))
								.binding(defaults.uiAndVisuals.world.hideMobSpawnerAnimations,
										() -> config.uiAndVisuals.world.hideMobSpawnerAnimations,
										newValue -> config.uiAndVisuals.world.hideMobSpawnerAnimations = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide Lightning"))
								.description(OptionDescription.of(Text.literal("Hides lightning bolts and lightning flashes in the sky..")))
								.binding(defaults.uiAndVisuals.world.hideLightning,
										() -> config.uiAndVisuals.world.hideLightning,
										newValue -> config.uiAndVisuals.world.hideLightning = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide Fog"))
								.description(OptionDescription.of(Text.literal("Hides terrain fog.")))
								.binding(defaults.uiAndVisuals.world.hideFog,
										() -> config.uiAndVisuals.world.hideFog,
										newValue -> config.uiAndVisuals.world.hideFog = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Correct Ambient Darkness"))
								.description(OptionDescription.of(Text.literal("Fixes an issue causing overlays, vignettes, and entity shadows to render as if it was always daytime (MC-259651).")))
								.binding(defaults.uiAndVisuals.world.correctAmbientDarkness,
										() -> config.uiAndVisuals.world.correctAmbientDarkness,
										newValue -> config.uiAndVisuals.world.correctAmbientDarkness = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.build())

				//Legacy Revival - "A Return to the Classics"
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Legacy Revival"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Old Message Trust Indicator Colours"))
								.description(OptionDescription.of(Text.literal("Changes the colour used for the ")
										.append(Text.literal("Not Secure").withColor(0xe84f58))
										.append(Text.literal(" and "))
										.append(Text.literal("Modified").withColor(0xeac864))
										.append(Text.literal(" trust indicators to what they were in 1.19.2 for better visual distinction."))))
								.binding(defaults.uiAndVisuals.legacyRevival.oldMessageTrustIndicatorColours,
										() -> config.uiAndVisuals.legacyRevival.oldMessageTrustIndicatorColours,
										newValue -> config.uiAndVisuals.legacyRevival.oldMessageTrustIndicatorColours = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Potion Glint"))
								.description(OptionDescription.of(Text.literal("Restores the enchantment glint on potions.")))
								.binding(defaults.uiAndVisuals.legacyRevival.potionGlint,
										() -> config.uiAndVisuals.legacyRevival.potionGlint,
										newValue -> config.uiAndVisuals.legacyRevival.potionGlint = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.build())

				//Inventory Screen
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Inventory Screen"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Separate Inventory GUI Scale"))
								.description(OptionDescription.of(Text.literal("Allows you to set a separate GUI Scale for inventory/container screens.")))
								.binding(defaults.uiAndVisuals.inventoryScreen.separateInventoryGuiScale,
										() -> config.uiAndVisuals.inventoryScreen.separateInventoryGuiScale,
										newValue -> config.uiAndVisuals.inventoryScreen.separateInventoryGuiScale = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Text.literal("Inventory GUI Scale"))
								.description(OptionDescription.of(Text.literal("If you have the Separate Inventory GUI Scale option enabled then this will be the GUI Scale used for all inventory/container screens.")))
								.binding(defaults.uiAndVisuals.inventoryScreen.inventoryGuiScale,
										() -> config.uiAndVisuals.inventoryScreen.inventoryGuiScale,
										newValue -> config.uiAndVisuals.inventoryScreen.inventoryGuiScale = newValue)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt)
										.range(0, SeparateInventoryGuiScale.getAutoGuiScale())
										.step(1)
										.formatValue(scale -> scale == 0 ? Text.literal("Auto") : Text.literal(scale + "x")))
								.build())
						.build())

				//Image Preview
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Image Preview"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Enable Image Preview"))
								.description(OptionDescription.of(Text.literal("When hovering over a link to an image in chat, a preview of that image will be displayed.")
										.append(Text.literal("\n\nOnly PNG and JPEG images from these hosts can be previewed: cdn.discordapp.com, media.discordapp.net, and i.imgur.com."))))
								.binding(defaults.uiAndVisuals.imagePreview.enableImagePreview,
										() -> config.uiAndVisuals.imagePreview.enableImagePreview,
										newValue -> config.uiAndVisuals.imagePreview.enableImagePreview = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Image Preview Scale"))
								.description(OptionDescription.of(Text.literal("Change the scaling of previewed images.")))
								.binding(defaults.uiAndVisuals.imagePreview.scale,
										() -> config.uiAndVisuals.imagePreview.scale,
										newValue -> config.uiAndVisuals.imagePreview.scale = newValue)
								.controller(ConfigUtils::createFloatMultFieldController)
								.build())
						.build())

				//Chroma Text
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Chroma Text"))
						.collapsed(true)
						.option(Option.<Integer>createBuilder()
								.name(Text.literal("Chroma Size"))
								.description(OptionDescription.of(Text.literal("Allows you to change the size of colours in the chroma gradient. The value must be between 1 and 200.")))
								.binding(defaults.uiAndVisuals.chromaText.chromaSize,
										() -> config.uiAndVisuals.chromaText.chromaSize,
										newValue -> config.uiAndVisuals.chromaText.chromaSize = newValue)
								.controller(opt -> ConfigUtils.createIntPercentageFieldController(opt, c -> c.range(1, 200)))
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Chroma Speed"))
								.description(OptionDescription.of(Text.literal("The speed at which the chroma text effect will move between colours. The value must be between 1 and 64.")))
								.binding(defaults.uiAndVisuals.chromaText.chromaSpeed,
										() -> config.uiAndVisuals.chromaText.chromaSpeed,
										newValue -> config.uiAndVisuals.chromaText.chromaSpeed = newValue)
								.controller(opt -> FloatFieldControllerBuilder.create(opt).range(1f, 64f))
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Chroma Saturation"))
								.description(OptionDescription.of(Text.literal("How saturated the chroma text colours will be. The value must be between 0 and 1 (0-100%).")))
								.binding(defaults.uiAndVisuals.chromaText.chromaSaturation,
										() -> config.uiAndVisuals.chromaText.chromaSaturation,
										newValue -> config.uiAndVisuals.chromaText.chromaSaturation = newValue)
								.controller(opt -> FloatFieldControllerBuilder.create(opt).range(0f, 1f))
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Disable Chroma Pack"))
								.description(OptionDescription.of(Text.literal("Enabling this option allows you to disable the built-in chroma text resourcepack. Only turn this on if you actually want to turn off the pack otherwise it may misbehave and not work properly.")))
								.binding(defaults.uiAndVisuals.chromaText.canDisableChromaPack,
										() -> config.uiAndVisuals.chromaText.canDisableChromaPack,
										newValue -> config.uiAndVisuals.chromaText.canDisableChromaPack = newValue)
								.controller(ConfigUtils::createBooleanController)
								.flag(OptionFlag.GAME_RESTART)
								.build())
						.build())

				//Seasonal
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Seasonal"))
						.description(OptionDescription.of(Text.literal("Options which take effect on a seasonal basis.")))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("December Christmas Chests"))
								.description(OptionDescription.of(Text.literal("You'll be able to see the Christmas chests for the entire month of December!\n\nNote: This option only takes effect during the month of December.")))
								.binding(defaults.uiAndVisuals.seasonal.decemberChristmasChests,
										() -> config.uiAndVisuals.seasonal.decemberChristmasChests,
										newValue -> config.uiAndVisuals.seasonal.decemberChristmasChests = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.build())

				.build();
	}
}

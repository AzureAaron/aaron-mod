package net.azureaaron.mod.config;

import java.awt.Color;
import java.nio.file.Path;
import java.util.Calendar;

import org.apache.commons.lang3.SystemUtils;

import com.google.gson.FieldNamingPolicy;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.azureaaron.mod.Colour;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.Particles;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.Skyblock;
import net.azureaaron.mod.utils.TextTransformer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class AaronModConfigManager {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final Calendar CALENDAR = Calendar.getInstance();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("aaron-mod.json");
	private static final ConfigClassHandler<AaronModConfig> HANDLER = ConfigClassHandler.createBuilder(AaronModConfig.class)
			.serializer(config -> GsonConfigSerializerBuilder.create(config)
					.setPath(PATH)
					.setJson5(false)
					.appendGsonBuilder(builder -> builder
							.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
							.registerTypeHierarchyAdapter(Identifier.class, new Identifier.Serializer()))
					.build())
			.build();
	
	public static void init() {
		if (StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass() != Main.class) {
			throw new RuntimeException("Aaron's Mod: Config initializer can only be called from the main class!");
		}
		
		HANDLER.load();
	}
	
	public static AaronModConfig get() {
		return HANDLER.instance();
	}
	
	public static void save() {
		HANDLER.save();
	}
	
	public static Screen createGui(Screen parent) {
		return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> builder
				.title(Text.literal("Aaron's Mod"))
				.category(ConfigCategory.createBuilder()
						.name(Text.literal("General"))
						.option(Option.<Colour.ColourProfiles>createBuilder()
								.name(Text.literal("Colour Profile"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Changes the colour of text used in commands!\n\nYou can choose from:\n")
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
												.append(Text.literal("and Ocean\n").withColor(Colour.ColourProfiles.Ocean.primaryColour.getAsInt()))

												.append(Text.literal("or you can make a "))
												.append(Text.literal("Custom").styled(style -> style.withItalic(true)))
												.append(Text.literal(" colour profile!")))
										.build())
								.binding(defaults.colourProfile,
										() -> config.colourProfile,
										newValue -> config.colourProfile = newValue)
								.controller(ConfigUtils::createEnumController)
								.build())
						.group(OptionGroup.createBuilder()
								.name(Text.literal("Custom Colour Profile"))
								.description(OptionDescription.of(Text.literal("In order to use this you must set the Colour Profile option to Custom!")))
								.collapsed(true)
								.option(Option.<Color>createBuilder()
										.name(Text.literal("Primary Colour"))
										.binding(defaults.customColourProfile.primaryColour,
												() -> config.customColourProfile.primaryColour,
												newValue -> config.customColourProfile.primaryColour = newValue)
										.controller(ColorControllerBuilder::create)
										.build())
								.option(Option.<Color>createBuilder()
										.name(Text.literal("Secondary Colour"))
										.binding(defaults.customColourProfile.secondaryColour,
												() -> config.customColourProfile.secondaryColour,
												newValue -> config.customColourProfile.secondaryColour = newValue)
										.controller(ColorControllerBuilder::create)
										.build())
								.option(Option.<Color>createBuilder()
										.name(Text.literal("Info Colour"))
										.binding(defaults.customColourProfile.infoColour,
												() -> config.customColourProfile.infoColour,
												newValue -> config.customColourProfile.infoColour = newValue)
										.controller(ColorControllerBuilder::create)
										.build())
								.option(Option.<Color>createBuilder()
										.name(Text.literal("Highlight Colour"))
										.binding(defaults.customColourProfile.highlightColour,
												() -> config.customColourProfile.highlightColour,
												newValue -> config.customColourProfile.highlightColour = newValue)
										.controller(ColorControllerBuilder::create)
										.build())
								.option(Option.<Color>createBuilder()
										.name(Text.literal("Hover Colour"))
										.binding(defaults.customColourProfile.hoverColour,
												() -> config.customColourProfile.hoverColour,
												newValue -> config.customColourProfile.hoverColour = newValue)
										.controller(ColorControllerBuilder::create)
										.build())
								.option(Option.<Color>createBuilder()
										.name(Text.literal("Supporting Info Colour"))
										.binding(defaults.customColourProfile.supportingInfoColour,
												() -> config.customColourProfile.supportingInfoColour,
												newValue -> config.customColourProfile.supportingInfoColour = newValue)
										.controller(ColorControllerBuilder::create)
										.build())
								.build())
						.group(OptionGroup.createBuilder()
								.name(Text.literal("Display"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Options which add or modify existing display elements in the game."))
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Shadowed Scoreboard Text"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Adds text shadowing to the scoreboard!"))
												.build())
										.binding(defaults.shadowedScoreboard,
												() -> config.shadowedScoreboard,
												newValue -> config.shadowedScoreboard = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Hide Scoreboard Score"))
										.description(OptionDescription.of(Text.literal("Hides the numbers on the far right of the scoreboard.")))
										.binding(defaults.hideScoreboardScore,
												() -> config.hideScoreboardScore,
												newValue -> config.hideScoreboardScore = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Shadowed Name Tag Text"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Adds text shadowing to name tags!"))
												.build())
										.binding(defaults.shadowedNametags,
												() -> config.shadowedNametags,
												newValue -> config.shadowedNametags = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Hide Name Tag Background"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Hides the background in name tags."))
												.build())
										.binding(defaults.hideNametagBackground,
												() -> config.hideNametagBackground,
												newValue -> config.hideNametagBackground = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Status Effect Background Opacity"))
										.description(OptionDescription.of(Text.literal("Allows you to change the opacity of the background behind status effects in the HUD.\n\nSet this to 0 if you want to hide the background.")))
										.binding(defaults.statusEffectBackgroundAlpha,
												() -> config.statusEffectBackgroundAlpha,
												newValue -> config.statusEffectBackgroundAlpha = newValue)
										.controller(opt -> ConfigUtils.createFloatSliderController(opt, controller -> controller.range(0f, 1f).step(0.05f)))
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("FPS Display"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Displays your FPS in the top left corner of your screen!"))
												.build())
										.binding(defaults.fpsDisplay,
												() -> config.fpsDisplay,
												newValue -> config.fpsDisplay = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Extra Debug Info"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Adds some extra information to the F3 menu."))
												.build())
										.binding(defaults.extraDebugInfo, 
												() -> config.extraDebugInfo, 
												newValue -> config.extraDebugInfo = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Always Show Day in F3"))
										.description(OptionDescription.of(Text.literal("Due to an oversight in the fix of MC-230732 the world's day counter in the F3 menu is hidden outside of singleplayer, this option brings it back when outside of singleplayer.")))
										.binding(defaults.alwaysShowDayInF3,
												() -> config.alwaysShowDayInF3,
												newValue -> config.alwaysShowDayInF3 = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Hide Tutorials"))
										.description(OptionDescription.of(Text.literal("Hides tutorial pop-up toasts.")))
										.binding(defaults.hideTutorials,
												() -> config.hideTutorials,
												newValue -> config.hideTutorials = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Hide Fire Overlay"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Prevents the fire overlay from being seen while you're burning."))
												.build())
										.binding(defaults.hideFireOverlay,
												() -> config.hideFireOverlay, 
												newValue -> config.hideFireOverlay = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Hide Lightning"))
										.description(OptionDescription.of(Text.literal("Hides lightning bolts and lightning flashes in the sky.")))
										.binding(defaults.hideLightning,
												() -> config.hideLightning,
												newValue -> config.hideLightning = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Hide Mob Spawner Animations"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Hides the spinning mob inside of mob spawners aswell as also hiding the particles emitted by mob spawners.\n\nCan be useful in areas with lots of mob spawners!"))
												.build())
										.binding(defaults.hideSpinningMobInMobSpawner,
												() -> config.hideSpinningMobInMobSpawner,
												newValue -> config.hideSpinningMobInMobSpawner = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Correct Ambient Darkness"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Fixes an issue causing overlays, vignettes and shadows to render as if it was always daytime. (MC-259651)"))
												.build())
										.binding(defaults.correctAmbientDarkness,
												() -> config.correctAmbientDarkness,
												newValue -> config.correctAmbientDarkness = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Double>createBuilder()
										.name(Text.literal("Zoom Multiplier"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Modifies how much your screen will zoom in by when using the zoom feature."))
												.build())
										.binding(defaults.zoomMultiplier,
												() -> config.zoomMultiplier,
												newValue -> config.zoomMultiplier = newValue)
										.controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.15d, 0.45d).step(0.01d))
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Old Message Trust Indicator Colours"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Changes the colour used for the ")
														.append(Text.literal("Not Secure").styled(style -> style.withColor(0xe84f58)))
														.append(Text.literal(" and "))
														.append(Text.literal("Modified").styled(style -> style.withColor(0xeac864)))
														.append(Text.literal(" trust indicators to what they were in 1.19.2 for better visual distinction.")))
												.build())
										.binding(defaults.oldMessageIndicatorColours,
												() -> config.oldMessageIndicatorColours, 
												newValue -> config.oldMessageIndicatorColours = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Chroma Text Animation Speed"))
										.description(OptionDescription.of(Text.literal("The speed at which the chroma text effect will move between colours. The value must be between 1 and 64.")))
										.binding(defaults.chromaSpeed,
												() -> config.chromaSpeed,
												newValue -> config.chromaSpeed = newValue)
										.controller(opt -> FloatFieldControllerBuilder.create(opt).range(1f, 64f))
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Chroma Text Saturation"))
										.description(OptionDescription.of(Text.literal("How saturated the chroma text colours will be. The value must be between 0 and 1 (0-100%).")))
										.binding(defaults.chromaSaturation,
												() -> config.chromaSaturation,
												newValue -> config.chromaSaturation = newValue)
										.controller(opt -> FloatFieldControllerBuilder.create(opt).range(0f, 1f))
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Potion Enchantment Glint"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Applies the enchantment glint to potions."))
												.build())
										.binding(defaults.shinyPotions,
												() -> config.shinyPotions,
												newValue -> config.shinyPotions = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.build())
						.group(OptionGroup.createBuilder()
								.name(Text.literal("Functional"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Options that control features which modify or introduce new functionality to the game."))
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Don't Stop Sounds on World Change"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Prevents sounds/music from being stopped upon changing worlds or exiting to the title screen."))
												.build())
										.binding(defaults.stopSoundsOnWorldChange,
												() -> config.stopSoundsOnWorldChange,
												newValue -> config.stopSoundsOnWorldChange = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Hide World Loading Screen"))
										.description(OptionDescription.of(Text.literal("Hides the screen shown when loading into worlds.")))
										.binding(defaults.hideWorldLoadingScreen,
												() -> config.hideWorldLoadingScreen,
												newValue -> config.hideWorldLoadingScreen = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("No Fog"))
										.description(OptionDescription.of(Text.literal("Disables fog in the world.")))
										.binding(defaults.noFog,
												() -> config.noFog,
												newValue -> config.noFog = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Copy Chat Messages"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Allows you to copy a chat message by middle/right clicking on it!"))
												.build())
										.binding(defaults.copyChatMessages,
												() -> config.copyChatMessages,
												newValue -> config.copyChatMessages = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<AaronModConfig.MouseButton>createBuilder()
										.name(Text.literal("Copy Chat Mouse Button"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Change the mouse button you use when copying chat! You can choose between middle click and right click!"))
												.build())
										.binding(defaults.copyChatMouseButton,
												() -> config.copyChatMouseButton,
												newValue -> config.copyChatMouseButton = newValue)
										.controller(ConfigUtils::createEnumController)
										.build())
								.option(Option.<AaronModConfig.CopyChatMode>createBuilder()
										.name(Text.literal("Copy Chat Mode"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("The mod offers two different modes when copying chat messages:")
														.append(Text.literal("\n\nEntire Message: Copies the entire chat message."))
														.append(Text.literal("\n\nSingle Line: Copy chat messages line by line."))
														.append(Text.literal("\n\nTip: Holding down Alt/Option when copying an entire message will copy it as JSON.")))
												.build())
										.binding(defaults.copyChatMode,
												() -> config.copyChatMode,
												newValue -> config.copyChatMode = newValue)
										.controller(ConfigUtils::createEnumController)
										.available(config.copyChatMessages)
										.build())
								.option(Option.<Integer>createBuilder()
										.name(Text.literal("Chat History Length"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Change the maximum length of your chat history so that you don't miss any messages!")
														.append(Text.literal("\n\n⚠ Warning: Significantly higher values will lead to more memory usage.").styled(style -> style.withColor(0xeac864))))
												.build())
										.binding(defaults.chatHistoryLength,
												() -> config.chatHistoryLength,
												newValue -> config.chatHistoryLength = Math.max(100, newValue)) // If the value is somehow lower than 100
										.controller(opt -> IntegerFieldControllerBuilder.create(opt).min(100))
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Image Preview"))
										.description(OptionDescription.of(Text.literal("When hovering over a clickable image link in chat, a preview of that image will be displayed!")
												.append(Text.literal("\n\nOnly PNG and JPEG images from these hosts can be previewed: cdn.discordapp.com, media.discordapp.net and i.imgur.com"))))
										.binding(defaults.imagePreview,
												() -> config.imagePreview,
												newValue -> config.imagePreview = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Image Preview Scale"))
										.description(OptionDescription.of(Text.literal("Change the scaling of previewed images.")))
										.binding(defaults.imagePreviewScale,
												() -> config.imagePreviewScale,
												newValue -> config.imagePreviewScale = newValue)
										.controller(ConfigUtils::createFloatMultFieldController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Hotbar & Bundle Scroll Looping"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("When scrolling in the hotbar with this disabled, it prevents you from scrolling down to slot 9 from slot 1, and from scrolling up to slot 1 from slot 9.\n\nThis also works with scrolling between items in bundles!"))
												.build())
										.binding(defaults.infiniteHotbarScrolling,
												() -> config.infiniteHotbarScrolling,
												newValue -> config.infiniteHotbarScrolling = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Show Item Groups Outside of Creative"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("When enabled, item groups are appended to the lore of an item even when you're outside of creative."))
												.build())
										.binding(defaults.showItemGroupsOutsideOfCreative,
												() -> config.showItemGroupsOutsideOfCreative,
												newValue -> config.showItemGroupsOutsideOfCreative = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Don't Reset Cursor Position"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("When enabled, the position of your cursor doesn't reset between chest GUIs."))
												.build())
										.binding(defaults.resetCursorPosition,
												() -> config.resetCursorPosition,
												newValue -> config.resetCursorPosition = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Separate Inventory GUI Scale"))
										.description(OptionDescription.of(Text.literal("Allows you to set a separate GUI Scale for inventory/container screens.")))
										.binding(defaults.separateInventoryGuiScale,
												() -> config.separateInventoryGuiScale,
												newValue -> config.separateInventoryGuiScale = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Integer>createBuilder()
										.name(Text.literal("Inventory GUI Scale"))
										.description(OptionDescription.of(Text.literal("If you have the Separate Inventory GUI Scale option enabled then this will be the GUI scale used for all inventory/container screens.")))
										.binding(defaults.inventoryGuiScale,
												() -> config.inventoryGuiScale,
												newValue -> config.inventoryGuiScale = newValue)
										.controller(opt -> IntegerSliderControllerBuilder.create(opt)
												.step(1)
												.range(0, CLIENT.isRunning() ? CLIENT.getWindow().calculateScaleFactor(0, CLIENT.forcesUnicodeFont()) : 0)
												.formatValue(scale -> scale == 0 ? Text.literal("Auto") : Text.literal(scale + "x")))
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Optimized Screenshots"))
										.description(OptionDescription.of(Text.literal("Saves screenshots without the alpha channel which is unused, reducing file sizes by ~11%.")))
										.binding(defaults.optimizedScreenshots,
												() -> config.optimizedScreenshots,
												newValue -> config.optimizedScreenshots = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Silence Resource Pack Log Spam"))
										.description(OptionDescription.of(Text.literal("Silences those pesky errors complaining about resource pack directories not existing.")))
										.binding(defaults.silenceResourcePackLogSpam,
												() -> config.silenceResourcePackLogSpam,
												newValue -> config.silenceResourcePackLogSpam = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Secure Skin Downloads"))
										.description(OptionDescription.of(Text.literal("By default Minecraft downloads skins over insecure http, with this enabled skins are instead downloaded over https.")))
										.binding(defaults.secureSkinDownloads,
												() -> config.secureSkinDownloads,
												newValue -> config.secureSkinDownloads = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Mac Only - Alt. FN+F3+N Keybind"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Adds FN+F3+J as an alternate keybind for FN+F3+N"))
												.build())
										.binding(defaults.alternateF3PlusNKey,
												() -> config.alternateF3PlusNKey,
												newValue -> config.alternateF3PlusNKey = newValue)
										.controller(ConfigUtils::createBooleanController)
										.available(SystemUtils.IS_OS_MAC)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("December Christmas Chests"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("You'll be able to see the christmas chests for the entire month of December!"))
												.build())
										.binding(defaults.decemberChristmasChests,
												() -> config.decemberChristmasChests,
												newValue -> config.decemberChristmasChests = newValue)
										.controller(ConfigUtils::createBooleanController)
										.available(CALENDAR.get(Calendar.MONTH) + 1 == 12)
										.flag(OptionFlag.ASSET_RELOAD)
										.build())
								.build())
						.build())
				.category(ConfigCategory.createBuilder()
						.name(Text.literal("Hypixel"))
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Enable Skyblock Commands"))
								.description(OptionDescription.of(Text.literal("You can enable or disable the registration of the mod's skyblock commands.")))
								.binding(defaults.enableSkyblockCommands,
										() -> config.enableSkyblockCommands,
										newValue -> config.enableSkyblockCommands = newValue)
								.controller(ConfigUtils::createBooleanController)
								.flag(OptionFlag.GAME_RESTART)
								.build())
						.option(Option.<AaronModConfig.DayAverage>createBuilder()
								.name(Text.literal("Price Day Average"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Changes the day price average used in /lbin."))
										.build())
								.binding(defaults.dayAverage,
										() -> config.dayAverage,
										newValue -> config.dayAverage = newValue)
								.controller(ConfigUtils::createEnumController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Rainbowify Max Enchants"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Changes the text colour of maximum level enchantments in an item's lore to be a pretty rainbow gradient!\n\nCheck out of the 'Rainbowify Mode' option to see some examples!"))
										.build())
								.binding(defaults.rainbowifyMaxSkyblockEnchantments,
										() -> config.rainbowifyMaxSkyblockEnchantments,
										newValue -> config.rainbowifyMaxSkyblockEnchantments = newValue)
								.controller(ConfigUtils::createBooleanController)
								.addListener((opt, event) -> Functions.runIf(() -> Skyblock.loadMaxEnchants(true), () -> opt.pendingValue()))
								.build())
						.option(Option.<AaronModConfig.RainbowifyMode>createBuilder()
								.name(Text.literal("Rainbowify Mode"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Changes how the rainbow gradient will look:\n")
												.append(Text.literal("\nStill: "))
												.append(TextTransformer.rainbowify("Critical VII, Vampirism VI")) //H.H.
												.append(Text.literal("\nChroma: "))
												.append(Text.literal("Critical VII, Vampirism VI").styled(style -> style.withColor(0xAA5500)))) //H.H
										.build())
								.binding(defaults.rainbowifyMode,
										() -> config.rainbowifyMode,
										newValue -> config.rainbowifyMode = newValue)
								.controller(ConfigUtils::createEnumController)
								.build())
						.group(OptionGroup.createBuilder()
								.name(Text.literal("Dungeons"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Options relating to Skyblock Dungeons."))
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Party Finder Stats Lookup"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Automatically shows a player's dungeon stats when they join from party finder."))
												.build())
										.binding(defaults.dungeonFinderPersonStats,
												() -> config.dungeonFinderPersonStats,
												newValue -> config.dungeonFinderPersonStats = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Old Master Star Display"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Reverts the display of master stars in item names to how it used to be.\n\n")
														.append(TextTransformer.fromLegacy("Example: §dDark Claymore §6✪§6✪§6✪§6✪§6✪§c➎ §r→ §dDark Claymore §c✪✪✪✪✪")))
												.build())
										.binding(defaults.oldMasterStars,
												() -> config.oldMasterStars,
												newValue -> config.oldMasterStars = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Fancy Diamond Heads"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Diamond Heads will look a bit ")
														.append(Text.literal("fancier").styled(style -> style.withItalic(true)))
														.append(Text.literal("!")))
												.build())
										.binding(defaults.fancyDiamondHeads,
												() -> config.fancyDiamondHeads,
												newValue -> config.fancyDiamondHeads = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Hide Tooltips In Click On Time"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Hides the tooltips inside click on time so that they don't get in your way!"))
												.build())
										.binding(defaults.hideClickOnTimeTooltips,
												() -> config.hideClickOnTimeTooltips,
												newValue -> config.hideClickOnTimeTooltips = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("M7 Dragon Bounding Box"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Displays bounding boxes around the dragon statues in M7."))
												.build())
										.binding(defaults.masterModeF7DragonBoxes,
												() -> config.masterModeF7DragonBoxes,
												newValue -> config.masterModeF7DragonBoxes = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("M7 Dragon Spawn Timers"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Displays a timer under each statue that counts down to when the dragon spawns."))
												.build())
										.binding(defaults.m7DragonSpawnTimers,
												() -> config.m7DragonSpawnTimers,
												newValue -> config.m7DragonSpawnTimers = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("M7 Dragon Spawn Notifications"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Displays a title notification and plays a sound to notify you that a dragon will spawn soon."))
												.build())
										.binding(defaults.m7DragonSpawnNotifications,
												() -> config.m7DragonSpawnNotifications,
												newValue -> config.m7DragonSpawnNotifications = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("M7 Dragon Health Display"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Displays the health of a dragon underneath it."))
												.build())
										.binding(defaults.m7DragonHealth,
												() -> config.m7DragonHealth,
												newValue -> config.m7DragonHealth = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("M7 Dragon Aim Waypoints"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Displays waypoints to the location that you need to aim at in order to shoot the at dragon."))
												.build())
										.binding(defaults.m7ShootWaypoints,
												() -> config.m7ShootWaypoints,
												newValue -> config.m7ShootWaypoints = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("M7 Arrow Stack Waypoints"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Displays waypoints for arrow stacks."))
												.build())
										.binding(defaults.m7StackWaypoints,
												() -> config.m7StackWaypoints,
												newValue -> config.m7StackWaypoints = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.option(Option.<Boolean>createBuilder()
										.name(Text.literal("Glowing M7 Dragons"))
										.description(OptionDescription.createBuilder()
												.text(Text.literal("Adds a glowing outline to the dragons in M7, making them easier to spot!\n\nThe colour of the glow corresponds with the dragon's colour!"))
												.build())
										.binding(defaults.glowingM7Dragons,
												() -> config.glowingM7Dragons,
												newValue -> config.glowingM7Dragons = newValue)
										.controller(ConfigUtils::createBooleanController)
										.build())
								.build())
						.build())
				
				.category(ConfigCategory.createBuilder()
						.name(Text.literal("Particles"))
						.groups(Particles.getOptionGroups(config))
						.build())
				
				.category(ConfigCategory.createBuilder()
						.name(Text.literal("Text Replacer"))
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Enable Text Replacer"))
								.description(OptionDescription.of(Text.literal("The text replacer allows you to visually replace almost any text on screen with whatever you want!")
										.append(Text.literal("\n\nSpecial: Use HEX #AA5500 or &z for "))
										.append(Text.literal("chroma text").styled(style -> style.withColor(0xAA5500)))
										.append(Text.literal("!"))))
								.binding(defaults.visualTextReplacer,
										() -> config.visualTextReplacer,
										newValue -> config.visualTextReplacer = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						/*.option(ButtonOption.createBuilder()
								.name(Text.literal("Visual Text Replacer"))
								.text(Text.empty())
								.description(OptionDescription.of(Text.literal("Click to open the visual text replacer screen!")))
								.action((screen, opt) -> MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new TextReplacerConfigScreen(null))))
								.build())*/
						.option(ButtonOption.createBuilder()
								.name(Text.literal("How to use this! (Hover)"))
								.text(Text.empty())
								.description(OptionDescription.of(Text.literal("You can add text replacements with the command ")
										.append(Text.literal("/textreplacer add \"<textReplacement>\" <textComponent>").styled(style -> style.withColor(Formatting.GRAY)))
										.append(Text.literal("\n\nYou're able to remove text replacements with the command "))
										.append(Text.literal("/textreplacer remove \"<textReplacement>\"").styled(style -> style.withColor(Formatting.GRAY)))
										.append(Text.literal("\n\nIf you don't know how to create a text component use the website linked below, then copy n' paste the output!"))))
								.action((screen, opt) -> {}) //Do nothing I guess
								.build())
						.option(ButtonOption.createBuilder()
								.name(Text.literal("Text Component Generator Website"))
								.description(OptionDescription.of(Text.literal("Click to open a link to the website!")))
								.text(Text.literal("⧉"))
								.action((screen, opt) -> ConfirmLinkScreen.open(screen, "https://minecraft.tools/en/json_text.php"))
								.build())
						.build())
				.category(ConfigCategory.createBuilder()
						.name(Text.literal("Item Model"))
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Enable Item Model Customization"))
								.description(OptionDescription.of(Text.literal("Must be enabled in order for any of the options in this tab to work.")))
								.binding(defaults.itemModelCustomization.enableItemModelCustomization,
										() -> config.itemModelCustomization.enableItemModelCustomization,
										newValue -> config.itemModelCustomization.enableItemModelCustomization = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(ButtonOption.createBuilder()
								.name(Text.literal("How to use this! (Hover)"))
								.text(Text.empty())
								.description(OptionDescription.of(
										Text.literal("With these options, you can tweak the appearence of item models while they are held in first person."),
										Text.literal("\nYou can change the position, scale (size), and the rotation for items held in the main or off hand, as well as being able to customize the swing animation!")))
								.action((screen, opt) -> {}) //TODO make this BiConsumer a constant value somewhere for reuse
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Text.literal("Swing Duration"))
								.description(OptionDescription.of(Text.literal("How long the hand swing animation should last. Leave at 6 for the default/vanilla time.")))
								.binding(defaults.itemModelCustomization.swingDuration,
										() -> config.itemModelCustomization.swingDuration,
										newValue -> config.itemModelCustomization.swingDuration = newValue)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 16).step(1))
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Ignore Mining Effects"))
								.description(OptionDescription.of(Text.literal("Cancels the effect that Haste and Mining Fatigue have on the swing duration.")))
								.binding(defaults.itemModelCustomization.ignoreHaste,
										() -> config.itemModelCustomization.ignoreHaste,
										newValue -> config.itemModelCustomization.ignoreHaste = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())

						.group(OptionGroup.createBuilder()
								.name(Text.literal("Main Hand"))
								.description(OptionDescription.of(Text.literal("Transformations to apply to the item model in the main hand.")))
								.option(Option.<Float>createBuilder()
										.name(Text.literal("X Position"))
										.description(OptionDescription.of(Text.literal("The units to translate the x of the item model by.")))
										.binding(defaults.itemModelCustomization.mainHand.x,
												() -> config.itemModelCustomization.mainHand.x,
												newValue -> config.itemModelCustomization.mainHand.x = newValue)
										.controller(ConfigUtils::createFloatFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Y Position"))
										.description(OptionDescription.of(Text.literal("The units to translate the y of the item model by.")))
										.binding(defaults.itemModelCustomization.mainHand.y,
												() -> config.itemModelCustomization.mainHand.y,
												newValue -> config.itemModelCustomization.mainHand.y = newValue)
										.controller(ConfigUtils::createFloatFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Z Position"))
										.description(OptionDescription.of(Text.literal("The units to translate the z of the item model by.")))
										.binding(defaults.itemModelCustomization.mainHand.z,
												() -> config.itemModelCustomization.mainHand.z,
												newValue -> config.itemModelCustomization.mainHand.z = newValue)
										.controller(ConfigUtils::createFloatFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Scale"))
										.description(OptionDescription.of(Text.literal("The units to scale the item model by.")))
										.binding(defaults.itemModelCustomization.mainHand.scale,
												() -> config.itemModelCustomization.mainHand.scale,
												newValue -> config.itemModelCustomization.mainHand.scale = newValue)
										.controller(ConfigUtils::createFloatMultFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("X Rotation"))
										.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive x axis.")))
										.binding(defaults.itemModelCustomization.mainHand.xRotation,
												() -> config.itemModelCustomization.mainHand.xRotation,
												newValue -> config.itemModelCustomization.mainHand.xRotation = newValue)
										.controller(ConfigUtils::createFloatDegreesFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Y Rotation"))
										.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive y axis.")))
										.binding(defaults.itemModelCustomization.mainHand.yRotation,
												() -> config.itemModelCustomization.mainHand.yRotation,
												newValue -> config.itemModelCustomization.mainHand.yRotation = newValue)
										.controller(ConfigUtils::createFloatDegreesFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Z Rotation"))
										.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive z axis.")))
										.binding(defaults.itemModelCustomization.mainHand.zRotation,
												() -> config.itemModelCustomization.mainHand.zRotation,
												newValue -> config.itemModelCustomization.mainHand.zRotation = newValue)
										.controller(ConfigUtils::createFloatDegreesFieldController)
										.build())
								.build())

						.group(OptionGroup.createBuilder()
								.name(Text.literal("Off Hand"))
								.description(OptionDescription.of(Text.literal("Transformations to apply to the item model in the off hand.")))
								.option(Option.<Float>createBuilder()
										.name(Text.literal("X Position"))
										.description(OptionDescription.of(Text.literal("The units to translate the x of the item model by.")))
										.binding(defaults.itemModelCustomization.offHand.x,
												() -> config.itemModelCustomization.offHand.x,
												newValue -> config.itemModelCustomization.offHand.x = newValue)
										.controller(ConfigUtils::createFloatFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Y Position"))
										.description(OptionDescription.of(Text.literal("The units to translate the y of the item model by.")))
										.binding(defaults.itemModelCustomization.offHand.y,
												() -> config.itemModelCustomization.offHand.y,
												newValue -> config.itemModelCustomization.offHand.y = newValue)
										.controller(ConfigUtils::createFloatFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Z Position"))
										.description(OptionDescription.of(Text.literal("The units to translate the z of the item model by.")))
										.binding(defaults.itemModelCustomization.offHand.z,
												() -> config.itemModelCustomization.offHand.z,
												newValue -> config.itemModelCustomization.offHand.z = newValue)
										.controller(ConfigUtils::createFloatFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Scale"))
										.description(OptionDescription.of(Text.literal("The units to scale the item model by.")))
										.binding(defaults.itemModelCustomization.offHand.scale,
												() -> config.itemModelCustomization.offHand.scale,
												newValue -> config.itemModelCustomization.offHand.scale = newValue)
										.controller(ConfigUtils::createFloatMultFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("X Rotation"))
										.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive x axis.")))
										.binding(defaults.itemModelCustomization.offHand.xRotation,
												() -> config.itemModelCustomization.offHand.xRotation,
												newValue -> config.itemModelCustomization.offHand.xRotation = newValue)
										.controller(ConfigUtils::createFloatDegreesFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Y Rotation"))
										.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive y axis.")))
										.binding(defaults.itemModelCustomization.offHand.yRotation,
												() -> config.itemModelCustomization.offHand.yRotation,
												newValue -> config.itemModelCustomization.offHand.yRotation = newValue)
										.controller(ConfigUtils::createFloatDegreesFieldController)
										.build())
								.option(Option.<Float>createBuilder()
										.name(Text.literal("Z Rotation"))
										.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive z axis.")))
										.binding(defaults.itemModelCustomization.offHand.zRotation,
												() -> config.itemModelCustomization.offHand.zRotation,
												newValue -> config.itemModelCustomization.offHand.zRotation = newValue)
										.controller(ConfigUtils::createFloatDegreesFieldController)
										.build())
								.build())
						.build()))
				.generateScreen(parent);
	}
}

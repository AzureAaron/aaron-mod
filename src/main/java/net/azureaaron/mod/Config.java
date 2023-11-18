package net.azureaaron.mod;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.azureaaron.mod.annotations.ConfigEntry;
import net.azureaaron.mod.features.TextReplacer;
import net.azureaaron.mod.util.Constants;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.TextTransformer;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class Config {
	protected static void load() {
		try {
			if (!Files.exists(Main.CONFIG_PATH)) {
				Files.createDirectories(Main.CONFIG_PATH.getParent());
				Files.createFile(Main.CONFIG_PATH);
				Files.write(Main.CONFIG_PATH, "{}".getBytes(StandardCharsets.UTF_8));
				save();
				
				return;
			}
			
			JsonObject config = JsonParser.parseString(Files.readString(Main.CONFIG_PATH)).getAsJsonObject();
			Class<?> cls = Class.forName("net.azureaaron.mod.Config");
			Field[] fields = cls.getDeclaredFields();
        	
        	for (int i = 0; i < fields.length; i++) {
        		Field currentField = fields[i];
        		String fieldName = currentField.getName();
        		ConfigEntry annotation = currentField.getAnnotation(ConfigEntry.class);
        		
        		if (annotation != null) {
        			if (!annotation.isEnum()) {
        				switch (currentField.getType().getName()) {
        					case "boolean":
        						if (config.get(fieldName) != null) currentField.setBoolean(null, config.get(fieldName).getAsBoolean());
        						break;
        					case "int":
        						if (config.get(fieldName) != null) currentField.setInt(null, config.get(fieldName).getAsInt());
        						break;
        					case "float":
        						if (config.get(fieldName) != null) currentField.setFloat(null, config.get(fieldName).getAsFloat());
        						break;
        					case "double":
        						if (config.get(fieldName) != null) currentField.setDouble(null, config.get(fieldName).getAsDouble());
        						break;
        					case "java.lang.String":
        						if (config.get(fieldName) != null) currentField.set(null, config.get(fieldName).getAsString());
        						break;
        					default:
        						Main.LOGGER.warn("[Aaron's Mod] Encountered an unknown field type!");
        						break;
        				}
        			} else if (config.get(fieldName) != null) {
        				@SuppressWarnings({ "unchecked", "rawtypes" })
						Enum value = Enum.valueOf((Class<Enum>) currentField.getType(), config.get(fieldName).getAsString());
        				
        				currentField.set(null, value);
        			}
        		}
        	}
        	
        	if (config.get("textReplacer") != null) {
        		JsonObject textReplacerConfig = config.get("textReplacer").getAsJsonObject();
        		TextReplacer.deserializeAndLoad(textReplacerConfig.get("textReplacements").getAsJsonObject());
        	}
        	
        	Particles.init(config);
        	Colour.init(config);
		} catch (Exception e) {
			Main.LOGGER.error("[Aaron's Mod] Failed to load config!", e);
		}
	}
	
	//TODO maybe move this into the auctions class
	public enum DayAverage {
		ONE_DAY, 
		THREE_DAY, 
		SEVEN_DAY;
		
		@Override
		public String toString() {
			return Functions.titleCase(this.name().replace('_', ' '));
		}
	}
	
	public enum CopyChatMode {
		ENTIRE_MESSAGE,
		SINGLE_LINE;
		
		@Override
		public String toString() {
			return Functions.titleCase(this.name().replace('_', ' '));
		}
	}
	
	public enum RainbowifyMode {
		STATIC,
		DYNAMIC;
		
		@Override
		public String toString() {
			return switch (this) {
				case STATIC -> "Still";
				case DYNAMIC -> "Chroma";
			};
		}
	}
	
	public enum MouseButton {
		RIGHT,
		MIDDLE;
		
		@Override
		public String toString() {
			return Functions.titleCase(this.name()) + " Button";
		}
	}
	
	@ConfigEntry public static boolean shadowedScoreboard = true;
	@ConfigEntry public static boolean dungeonFinderPersonStats = true;
	@ConfigEntry(isEnum = true) public static DayAverage dayAverage = DayAverage.THREE_DAY;
	@ConfigEntry public static boolean alternateF3PlusNKey = SystemUtils.IS_OS_MAC;
	@ConfigEntry public static boolean dungeonScoreMessage = true;
	@ConfigEntry public static String twoHundredSeventyScore = "270 Score Reached!";
	@ConfigEntry public static String threeHundredScore = "300 Score Reached!";
	@ConfigEntry public static boolean oldMasterStars = false;
	@ConfigEntry public static boolean stopSoundsOnWorldChange = false;
	@ConfigEntry public static boolean fancyDiamondHeads = false;
	@ConfigEntry public static boolean fpsDisplay = true;
	@ConfigEntry public static boolean copyChatMessages = true;
	@ConfigEntry public static boolean infiniteHotbarScrolling = true;
	@ConfigEntry public static boolean showItemGroupsOutsideOfCreative = false;
	@ConfigEntry public static boolean extraDebugInfo = true;
	@ConfigEntry public static boolean resetCursorPosition = false;
	@ConfigEntry public static boolean decemberChristmasChests = false;
	@ConfigEntry public static boolean masterModeF7DragonBoxes = false;
	@ConfigEntry public static boolean colourfulPartyFinderNotes = false;
	@ConfigEntry public static boolean hideFireOverlay = false;
	@ConfigEntry public static boolean hideClickOnTimeTooltips = true;
	@ConfigEntry public static boolean oldMessageIndicatorColours = false;
	@ConfigEntry public static int chatHistoryLength = 100;
	@ConfigEntry public static double zoomMultiplier = 0.30d;
	@ConfigEntry public static boolean correctAmbientDarkness = true;
	@ConfigEntry public static boolean shadowedNametags = true;
	@ConfigEntry public static boolean shinyPotions = false;
	@ConfigEntry(isEnum = true) public static CopyChatMode copyChatMode = CopyChatMode.ENTIRE_MESSAGE;
	@ConfigEntry public static boolean hideSpinningMobInMobSpawner = false;
	@ConfigEntry public static boolean rainbowifyMaxSkyblockEnchantments = false;
	@ConfigEntry public static boolean glowingM7Dragons = false;
	@ConfigEntry(isEnum = true) public static RainbowifyMode rainbowifyMode = RainbowifyMode.DYNAMIC;
	@ConfigEntry(isEnum = true) public static MouseButton copyChatMouseButton = MouseButton.MIDDLE;
	@ConfigEntry public static boolean fixTabTranslucency = true;
	@ConfigEntry public static boolean hideNametagBackground = false;
	@ConfigEntry public static boolean m7DragonSpawnTimers = false;
	@ConfigEntry public static boolean m7GyroWaypoints = false;
	@ConfigEntry public static boolean m7ShootWaypoints = false;
	@ConfigEntry public static boolean m7StackWaypoints = false;
	@ConfigEntry public static boolean visualTextReplacer = false;
	@ConfigEntry public static boolean imagePreview = true;
	@ConfigEntry public static boolean m7DragonHealth = false;
	@ConfigEntry public static boolean optimizedScreenshots = false;
	@ConfigEntry public static float imagePreviewScale = 1f;
	@ConfigEntry public static boolean silenceResourcePackLogSpam = true;
	
	public static void save() {
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonObject config = new JsonObject();
			Class<?> cls = Class.forName("net.azureaaron.mod.Config");
        	Field[] fields = cls.getDeclaredFields();
        	
        	for (int i = 0; i < fields.length; i++) {
        		Field currentField = fields[i];
        		String fieldName = currentField.getName();
        		ConfigEntry annotation = currentField.getAnnotation(ConfigEntry.class);
        		
        		if (annotation != null) {
        			if (!annotation.isEnum()) {
        				switch (currentField.getType().getName()) {
        					case "boolean":
        						config.addProperty(fieldName, currentField.getBoolean(null));
        						break;
        					case "int":
        						config.addProperty(fieldName, currentField.getInt(null));
        						break;
        					case "float":
        						config.addProperty(fieldName, currentField.getFloat(null));
        						break;
        					case "double":
        						config.addProperty(fieldName, currentField.getDouble(null));
        						break;
        					case "java.lang.String":
        						config.addProperty(fieldName, currentField.get(null).toString());
        						break;
        					default:
        						Main.LOGGER.warn("[Aaron's Mod] Encountered an unknown field type!");
        						break;
        				}
        			} else {
        				String enumName = ((Enum<?>) currentField.get(null)).name();
        				config.addProperty(fieldName, enumName);
        			}
        		}
        	}
        	
        	//Colour Profile
        	config.addProperty("colourProfile", Colour.colourProfile.name());
        	
        	//Particle States
    		config.add("particles", Util.make(new JsonObject(), states -> {
        		for (Map.Entry<Identifier, Particles.State> entry : Particles.PARTICLE_STATES.entrySet()) {
        			states.addProperty(entry.getKey().toString(), entry.getValue().name());
        		}
    		}));
    		
    		//Particle Scale
    		config.add("particleScaling", Util.make(new JsonObject(), scales -> {
        		for (Object2FloatMap.Entry<Identifier> entry : Particles.PARTICLE_SCALES.object2FloatEntrySet()) {
        			scales.addProperty(entry.getKey().toString(), entry.getFloatValue());
        		}
    		}));
    		
    		//Visual Text Replacer    		
    		config.add("textReplacer", Util.make(new JsonObject(), tr -> {
    			tr.add("textReplacements", TextReplacer.serialize());
    		}));
    		
			Files.write(Main.CONFIG_PATH, gson.toJson(config).getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			Main.LOGGER.error("[Aaron's Mod] Failed to save config!", e);
		}
	}
	
	private static final Calendar CALENDAR = Calendar.getInstance();
	
	/**Placeholder variables for configuration values that are set through JVM flags for example.*/
	@SuppressWarnings("unused")
	private static transient boolean placeholderBoolean = false;
	
	@SuppressWarnings("unchecked")
	static <E extends Enum<?>> CyclingListControllerBuilder<E> createCyclingListController4Enum(Option<E> opt) {
		E[] constants = (E[]) opt.binding().defaultValue().getClass().getEnumConstants();
		
		return CyclingListControllerBuilder.create(opt).values(constants).formatValue(c -> Text.of(c.toString()));
	}
	
	private static BooleanControllerBuilder createBooleanController(Option<Boolean> opt) {
		return BooleanControllerBuilder.create(opt).coloured(true);
	}
	
	private static FloatFieldControllerBuilder createFloatMultFieldController(Option<Float> opt) {
		return FloatFieldControllerBuilder.create(opt).formatValue(f -> Text.of(f + "x"));
	}
			
	public static Screen createGui(Screen parent) {
		return YetAnotherConfigLib.createBuilder()
		.title(Text.literal("Aaron's Mod"))
		.category(ConfigCategory.createBuilder()
				.name(Text.literal("General"))
				.option(Option.<Colour.Colours>createBuilder()
						.name(Text.literal("Colour Profile"))
						.description(OptionDescription.createBuilder()
								.text(Text.literal("Changes the colour of text used in commands!\n\nYou can choose from:\n")
										.append(Text.literal("Original\n").styled(Constants.WITH_COLOUR.apply(Colour.Colours.Original.primaryColour)))
										.append(Text.literal("Midnight\n").styled(Constants.WITH_COLOUR.apply(Colour.Colours.Midnight.primaryColour)))
										.append(Text.literal("Earth\n").styled(Constants.WITH_COLOUR.apply(Colour.Colours.Earth.primaryColour)))
										.append(Text.literal("Sakura\n").styled(Constants.WITH_COLOUR.apply(Colour.Colours.Sakura.primaryColour)))
										.append(Text.literal("Cloudy\n").styled(Constants.WITH_COLOUR.apply(Colour.Colours.Cloudy.primaryColour)))
										.append(Text.literal("and Halloween").styled(Constants.WITH_COLOUR.apply(Colour.Colours.Halloween.primaryColour))))
								.build())
						.binding(Colour.Colours.Original,
								() -> Colour.colourProfile,
								newValue -> Colour.colourProfile = newValue)
						.controller(Config::createCyclingListController4Enum)
						.build())
				/*.option(Option.createBuilder(boolean.class)
						.name(Text.literal("Better Math"))
						.tooltip(Text.literal("Better Math optimizes the games math to make it more efficient. To toggle it you must add the following JVM flag: ")
								.append(Text.literal("-Daaronmod.useBetterMath=<true or false>").styled(style -> style.withColor(colourProfile.primaryColour))))
						.binding(false,
								() -> Main.USE_BETTER_MATH,
								newValue -> placeholderBoolean = newValue)
						.controller(Config::createBooleanController)
						.available(false)
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Math FMA"))
						.description(OptionDescription.createBuilder()
								.text(Text.literal("If your CPU supports it, you can enable FMA which minimizes the CPU time spent on some math calculations; you can enable it by adding this JVM flag: ")
										.append(Text.literal("-Djoml.useMathFma").styled(style -> style.withColor(colourProfile.primaryColour)))
										.append((Main.SUPPORTS_FMA ? Text.literal("\n\nThis CPU supports FMA!").styled(style -> style.withColor(0x1a7f37)) : Text.literal("\n\nThis CPU doesn't support FMA!").styled(style -> style.withColor(0xcf222e))))
										.append(Text.literal("\n\n\u26a0 Warning: Enabling FMA on an unsupported CPU can result in a significant performance hit!").styled(style -> style.withColor(0xeac864))))
								.build())
						.binding(false,
								() -> Options.USE_MATH_FMA,
								newValue -> placeholderBoolean = newValue)
						.controller(Config::createBooleanController)
						.available(false)
						.build())*/
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
								.binding(true,
										() -> shadowedScoreboard,
										newValue -> shadowedScoreboard = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Shadowed Name Tag Text"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Adds text shadowing to name tags!"))
										.build())
								.binding(true,
										() -> shadowedNametags,
										newValue -> shadowedNametags = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide Name Tag Background"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Hides the background in name tags."))
										.build())
								.binding(false,
										() -> hideNametagBackground,
										newValue -> hideNametagBackground = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Fix Tab Translucency"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Fixes an issue introduced in 1.20 where the tab's translucency is broken causing chat messages to render in front of it. (MC-263256)"))
										.build())
								.binding(true,
										() -> fixTabTranslucency,
										newValue -> fixTabTranslucency = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("FPS Display"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Displays your FPS in the top left corner of your screen!"))
										.build())
								.binding(true,
										() -> fpsDisplay,
										newValue -> fpsDisplay = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Extra Debug Info"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Adds some extra information to the F3 menu."))
										.build())
								.binding(true, 
										() -> extraDebugInfo, 
										newValue -> extraDebugInfo = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide Fire Overlay"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Prevents the fire overlay from being seen while you're burning."))
										.build())
								.binding(false,
										() -> hideFireOverlay, 
										newValue -> hideFireOverlay = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide Mob Spawner Animations"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Hides the spinning mob inside of mob spawners aswell as also hiding the particles emitted by mob spawners.\n\nCan be useful in areas with lots of mob spawners!"))
										.build())
								.binding(false,
										() -> hideSpinningMobInMobSpawner,
										newValue -> hideSpinningMobInMobSpawner = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Correct Ambient Darkness"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Fixes an issue causing overlays, vignettes and shadows to render as if it was always daytime. (MC-259651)"))
										.build())
								.binding(true,
										() -> correctAmbientDarkness,
										newValue -> correctAmbientDarkness = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Double>createBuilder()
								.name(Text.literal("Zoom Multiplier"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Modifies how much your screen will zoom in by when using the zoom feature."))
										.build())
								.binding(0.30d,
										() -> zoomMultiplier,
										newValue -> zoomMultiplier = newValue)
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
								.binding(false,
										() -> oldMessageIndicatorColours, 
										newValue -> oldMessageIndicatorColours = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Potion Enchantment Glint"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Applies the enchantment glint to potions."))
										.build())
								.binding(false,
										() -> shinyPotions,
										newValue -> shinyPotions = newValue)
								.controller(Config::createBooleanController)
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
								.binding(false,
										() -> stopSoundsOnWorldChange,
										newValue -> stopSoundsOnWorldChange = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Copy Chat Messages"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Allows you to copy a chat message by middle/right clicking on it!"))
										.build())
								.binding(true,
										() -> copyChatMessages,
										newValue -> copyChatMessages = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<MouseButton>createBuilder()
								.name(Text.literal("Copy Chat Mouse Button"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Change the mouse button you use when copying chat! You can choose between middle click and right click!"))
										.build())
								.binding(MouseButton.MIDDLE,
										() -> copyChatMouseButton,
										newValue -> copyChatMouseButton = newValue)
								.controller(Config::createCyclingListController4Enum)
								.build())
						.option(Option.<CopyChatMode>createBuilder()
								.name(Text.literal("Copy Chat Mode"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("The mod offers two different modes when copying chat messages:")
												.append(Text.literal("\n\nEntire Message: Copies the entire chat message."))
												.append(Text.literal("\n\nSingle Line: Copy chat messages line by line.")))
										.build())
								.binding(CopyChatMode.ENTIRE_MESSAGE,
										() -> copyChatMode,
										newValue -> copyChatMode = newValue)
								.controller(Config::createCyclingListController4Enum)
								.available(copyChatMessages)
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Text.literal("Chat History Length"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Change the maximum length of your chat history so that you don't miss any messages!")
												.append(Text.literal("\n\n\u26a0 Warning: Significantly higher values will lead to more memory usage.").styled(style -> style.withColor(0xeac864))))
										.build())
								.binding(100,
										() -> chatHistoryLength,
										newValue -> chatHistoryLength = Math.max(100, newValue)) // If the value is somehow lower than 100
								.controller(opt -> IntegerFieldControllerBuilder.create(opt).min(100))
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Image Preview"))
								.description(OptionDescription.of(Text.literal("When hovering over a clickable image link in chat, a preview of that image will be displayed!")
										.append(Text.literal("\n\nOnly images from these hosts can be previewed: cdn.discordapp.com, media.discordapp.net and i.imgur.com"))))
								.binding(true,
										() -> imagePreview,
										newValue -> imagePreview = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Image Preview Scale"))
								.description(OptionDescription.of(Text.literal("Change the scaling of previewed images.")))
								.binding(1f,
										() -> imagePreviewScale,
										newValue -> imagePreviewScale = newValue)
								.controller(Config::createFloatMultFieldController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Infinite Hotbar Scrolling"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("When scrolling in the hotbar with this disabled, it prevents you from scrolling down to slot 9 from slot 1, and from scrolling up to slot 1 from slot 9."))
										.build())
								.binding(true,
										() -> infiniteHotbarScrolling,
										newValue -> infiniteHotbarScrolling = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Show Item Groups Outside of Creative"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("When enabled, item groups are appended to the lore of an item even when you're outside of creative."))
										.build())
								.binding(false,
										() -> showItemGroupsOutsideOfCreative,
										newValue -> showItemGroupsOutsideOfCreative = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Don't Reset Cursor Position"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("When enabled, the position of your cursor doesn't reset between chest GUIs."))
										.build())
								.binding(false,
										() -> resetCursorPosition,
										newValue -> resetCursorPosition = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Optimized Screenshots"))
								.description(OptionDescription.of(Text.literal("Saves screenshots without the alpha channel which is unused, reducing file sizes by ~11%.")))
								.binding(false,
										() -> optimizedScreenshots,
										newValue -> optimizedScreenshots = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Silence Resource Pack Log Spam"))
								.description(OptionDescription.of(Text.literal("Silences those pesky errors complaining about resource pack directories not existing.")))
								.binding(false,
										() -> silenceResourcePackLogSpam,
										newValue -> silenceResourcePackLogSpam = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Mac Only - Alt. FN+F3+N Keybind"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Adds FN+F3+J as an alternate keybind for FN+F3+N"))
										.build())
								.binding(SystemUtils.IS_OS_MAC,
										() -> alternateF3PlusNKey,
										newValue -> alternateF3PlusNKey = newValue)
								.controller(Config::createBooleanController)
								.available(SystemUtils.IS_OS_MAC)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("December Christmas Chests"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("You'll be able to see the christmas chests for the entire month of December!"))
										.build())
								.binding(false,
										() -> decemberChristmasChests,
										newValue -> decemberChristmasChests = newValue)
								.controller(Config::createBooleanController)
								.available(CALENDAR.get(Calendar.MONTH) + 1 == 12)
								.flag(OptionFlag.ASSET_RELOAD)
								.build())
						.build())
				.build())
		.category(ConfigCategory.createBuilder()
				.name(Text.literal("Hypixel"))
				.option(Option.<DayAverage>createBuilder()
						.name(Text.literal("Price Day Average"))
						.description(OptionDescription.createBuilder()
								.text(Text.literal("Changes the day price average used in /lbin."))
								.build())
						.binding(DayAverage.THREE_DAY,
								() -> dayAverage,
								newValue -> dayAverage = newValue)
						.controller(Config::createCyclingListController4Enum)
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Rainbowify Max Enchants"))
						.description(OptionDescription.createBuilder()
								.text(Text.literal("Changes the text colour of maximum level enchantments in an item's lore to be a pretty rainbow gradient!\n\nCheck out of the 'Rainbowify Mode' option to see some examples!"))
								.build())
						.binding(false,
								() -> rainbowifyMaxSkyblockEnchantments,
								newValue -> rainbowifyMaxSkyblockEnchantments = newValue)
						.controller(Config::createBooleanController)
						.build())
				.option(Option.<RainbowifyMode>createBuilder()
						.name(Text.literal("Rainbowify Mode"))
						.description(OptionDescription.createBuilder()
								.text(Text.literal("Changes how the rainbow gradient will look:\n")
										.append(Text.literal("\nStill: "))
										.append(TextTransformer.rainbowify("Critical VII, Vampirism VI")) //H.H.
										.append(Text.literal("\nChroma: "))
										.append(Text.literal("Critical VII, Vampirism VI").styled(style -> style.withColor(0xAA5500)))) //H.H
								.build())
						.binding(RainbowifyMode.DYNAMIC,
								() -> rainbowifyMode,
								newValue -> rainbowifyMode = newValue)
						.controller(Config::createCyclingListController4Enum)
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
								.binding(true,
										() -> dungeonFinderPersonStats,
										newValue -> dungeonFinderPersonStats = newValue)
								.controller(Config::createBooleanController)
								.build())
						/*.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Colourful Party Finder Notes"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Allows you to stylize your party finder notes by using colour formatting codes!"))
										.build())
								.binding(false,
										() -> colourfulPartyFinderNotes,
										newValue -> colourfulPartyFinderNotes = newValue)
								.controller(Config::createBooleanController)
								.build())*/
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Dungeon Score Message"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Sends a message when 270 or 300 score is reached! Messages can be up to 244 characters in length."))
										.build())
								.binding(true,
										() -> dungeonScoreMessage,
										newValue -> dungeonScoreMessage = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<String>createBuilder()
								.name(Text.literal("270 Score Message"))
								.binding("270 Score Reached!",
										() -> twoHundredSeventyScore,
										newValue -> twoHundredSeventyScore = newValue)
								.controller(opt -> StringControllerBuilder.create(opt))
								.available(dungeonScoreMessage)
								.build())
						.option(Option.<String>createBuilder()
								.name(Text.literal("300 Score Message"))
								.binding("300 Score Reached!",
										() -> threeHundredScore,
										newValue -> threeHundredScore = newValue)
								.controller(opt -> StringControllerBuilder.create(opt))
								.available(dungeonScoreMessage)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Old Master Star Display"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Reverts the display of master stars in item names to how it used to be.\n\n")
												.append(TextTransformer.fromLegacy("Example: §dDark Claymore §6✪§6✪§6✪§6✪§6✪§c➎ §r→ §dDark Claymore §c✪✪✪✪✪")))
										.build())
								.binding(false,
										() -> oldMasterStars,
										newValue -> oldMasterStars = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Fancy Diamond Heads"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Diamond Heads will look a bit ")
												.append(Text.literal("fancier").styled(style -> style.withItalic(true)))
												.append(Text.literal("!")))
										.build())
								.binding(false,
										() -> fancyDiamondHeads,
										newValue -> fancyDiamondHeads = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Hide Tooltips In Click On Time"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Hides the tooltips inside click on time so that they don't get in your way!"))
										.build())
								.binding(true,
										() -> hideClickOnTimeTooltips,
										newValue -> hideClickOnTimeTooltips = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("M7 Dragon Bounding Box"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Displays bounding boxes around the dragon statues in M7."))
										.build())
								.binding(false,
										() -> masterModeF7DragonBoxes,
										newValue -> masterModeF7DragonBoxes = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("M7 Dragon Spawn Timers"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Displays a timer under each statue that counts down to when the dragon spawns."))
										.build())
								.binding(false,
										() -> m7DragonSpawnTimers,
										newValue -> m7DragonSpawnTimers = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("M7 Dragon Health Display"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Displays the health of a dragon underneath it."))
										.build())
								.binding(false,
										() -> m7DragonHealth,
										newValue -> m7DragonHealth = newValue)
								.controller(Config::createBooleanController)
								.build())
						/*.option(Option.<Boolean>createBuilder()
								.name(Text.literal("M7 Gyro Waypoints"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Displays gyro waypoints for the Ice and Apex dragons."))
										.build())
								.binding(false,
										() -> m7GyroWaypoints,
										newValue -> m7GyroWaypoints = newValue)
								.controller(Config::createBooleanController)
								.build())*/
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("M7 Dragon Aim Waypoints"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Displays waypoints to the location that you need to aim at in order to shoot the at dragon."))
										.build())
								.binding(false,
										() -> m7ShootWaypoints,
										newValue -> m7ShootWaypoints = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("M7 Arrow Stack Waypoints"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Displays waypoints for arrow stacks."))
										.build())
								.binding(false,
										() -> m7StackWaypoints,
										newValue -> m7StackWaypoints = newValue)
								.controller(Config::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Glowing M7 Dragons"))
								.description(OptionDescription.createBuilder()
										.text(Text.literal("Adds a glowing outline to the dragons in M7, making them easier to spot!\n\nThe colour of the glow corresponds with the dragon's colour!"))
										.build())
								.binding(false,
										() -> glowingM7Dragons,
										newValue -> glowingM7Dragons = newValue)
								.controller(Config::createBooleanController)
								.build())
						.build())
				.build())
		
		.category(ConfigCategory.createBuilder()
				.name(Text.literal("Particles"))
				.groups(Particles.getOptionGroups())
				.build())
		
		.category(ConfigCategory.createBuilder()
				.name(Text.literal("Text Replacer"))
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Enable Text Replacer"))
						.description(OptionDescription.of(Text.literal("The text replacer allows you to visually replace almost any text on screen with whatever you want!")
								.append(Text.literal("\n\nSpecial: Use HEX #AA5500 or &z for "))
								.append(Text.literal("chroma text").styled(style -> style.withColor(0xAA5500)))
								.append(Text.literal("!"))))
						.binding(false,
								() -> visualTextReplacer,
								newValue -> visualTextReplacer = newValue)
						.controller(Config::createBooleanController)
						.build())
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
						.text(Text.literal("\u29C9"))
						.action((screen, opt) -> ConfirmLinkScreen.open("https://minecraft.tools/en/json_text.php", screen, false))
						.build())
				.build())
		.save(Config::save)
		.build()
		.generateScreen(parent);
	}
}

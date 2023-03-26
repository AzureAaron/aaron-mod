package net.azureaaron.mod;

import static net.azureaaron.mod.Colour.colourProfile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;

import org.joml.Options;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionFlag;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.BooleanController;
import dev.isxander.yacl.gui.controllers.cycling.CyclingListController;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.string.StringController;
import net.azureaaron.mod.annotations.ConfigEntry;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.TextTransformer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Config {
	protected static void load() {
		try {
			if(!Files.exists(Main.CONFIG_PATH)) {
				Files.createDirectories(Main.CONFIG_PATH.getParent());
				Files.createFile(Main.CONFIG_PATH);
				Files.write(Main.CONFIG_PATH, "{}".getBytes(StandardCharsets.UTF_8));
				save();
				return;
			}
			
			JsonObject config = JsonParser.parseString(Files.readString(Main.CONFIG_PATH)).getAsJsonObject();
			Class<?> cls = Class.forName("net.azureaaron.mod.Config");
			Field[] fields = cls.getDeclaredFields();
        	
        	for(int i = 0; i < fields.length; i++) {
        		Field currentField = fields[i];
        		String fieldName = currentField.getName();
        		ConfigEntry annotation = currentField.getAnnotation(ConfigEntry.class);
        		
        		if(annotation != null) {
        			if(!annotation.isEnum()) {
        				switch(currentField.getType().getName()) {
        				case "boolean":
        					if(config.get(fieldName) != null) currentField.setBoolean(null, config.get(fieldName).getAsBoolean());
        					break;
        				case "int":
        					if(config.get(fieldName) != null) currentField.setInt(null, config.get(fieldName).getAsInt());
        					break;
        				case "float":
        					if(config.get(fieldName) != null) currentField.setFloat(null, config.get(fieldName).getAsFloat());
        					break;
        				case "double":
        					if(config.get(fieldName) != null) currentField.setDouble(null, config.get(fieldName).getAsDouble());
        					break;
        				case "java.lang.String":
        					if(config.get(fieldName) != null) currentField.set(null, config.get(fieldName).getAsString());
        					break;
        				default:
        					Main.LOGGER.warn("[Aaron's Mod] Encountered an unknown field type!");
        					break;
        				}
        			} else if(config.get(fieldName) != null) {
        				//https://stackoverflow.com/questions/3735927/java-instantiating-an-enum-using-reflection
        				Method valueOf = currentField.getType().getMethod("valueOf", String.class);
        				Object value = valueOf.invoke(null, config.get(fieldName).getAsString());
        				currentField.set(null, value);
        			}
        		}
        	}
			
		} catch (IOException | ReflectiveOperationException e) {
			Main.LOGGER.error("[Aaron's Mod] Failed to load config!");
			e.printStackTrace();
		}
	}
	
	//TODO maybe move this into the auctions class
	public enum DayAverage {
		ONE_DAY, 
		THREE_DAY, 
		SEVEN_DAY;
	}
	
	public enum CopyChatMode {
		ENTIRE_MESSAGE,
		SINGLE_LINE;
	}
	
	@ConfigEntry public static String key = "";
	@ConfigEntry public static boolean shadowedScoreboard = true;
	@ConfigEntry public static boolean dungeonFinderPersonStats = true;
	@ConfigEntry(isEnum = true) public static DayAverage dayAverage = DayAverage.THREE_DAY;
	@ConfigEntry public static boolean alternateF3PlusNKey = MinecraftClient.IS_SYSTEM_MAC;
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
	
	private static void save() {
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonObject config = new JsonObject();
			Class<?> cls = Class.forName("net.azureaaron.mod.Config");
        	Field[] fields = cls.getDeclaredFields();
        	
        	for(int i = 0; i < fields.length; i++) {
        		Field currentField = fields[i];
        		String fieldName = currentField.getName();
        		ConfigEntry annotation = currentField.getAnnotation(ConfigEntry.class);
        		
        		if(annotation != null) {
        			if(!annotation.isEnum()) {
        				switch(currentField.getType().getName()) {
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
        	
        	//Particles
    		JsonObject particleConfig = new JsonObject();
    		for(Particles.ParticleConfig value : Particles.ParticleConfig.values()) {
    			particleConfig.addProperty(value.name(), value.state.name());
    		}
    		config.add("particles", particleConfig);
    		
			Files.write(Main.CONFIG_PATH, gson.toJson(config).getBytes(StandardCharsets.UTF_8));
		} catch (IOException | ReflectiveOperationException e) {
			Main.LOGGER.error("[Aaron's Mod] Failed to save config!");
			e.printStackTrace();
		}
	}
	
	private static final Calendar CALENDAR = Calendar.getInstance();
	
	/**Placeholder variables for configuration values that are set through JVM flags for example.*/
	@SuppressWarnings("unused")
	private static transient boolean placeholderBoolean = false;
	
	public static Screen createGui(Screen parent) {
		return YetAnotherConfigLib.createBuilder()
		.title(Text.literal("Aaron's Mod"))
		.category(ConfigCategory.createBuilder()
				.name(Text.literal("General"))
				.option(Option.createBuilder(Colour.Colours.class)
						.name(Text.literal("Colour Profile"))
						.tooltip(Text.literal("Changes the colour of text used in commands!\n\nYou can choose from:\n")
								.append(Text.literal("Original\n").styled(style -> style.withColor(Colour.Colours.Original.primaryColour)))
								.append(Text.literal("Midnight\n").styled(style -> style.withColor(Colour.Colours.Midnight.primaryColour)))
								.append(Text.literal("Earth\n").styled(style -> style.withColor(Colour.Colours.Earth.primaryColour)))
								.append(Text.literal("Sakura\n").styled(style -> style.withColor(Colour.Colours.Sakura.primaryColour)))
								.append(Text.literal("and Cloudy").styled(style -> style.withColor(Colour.Colours.Cloudy.primaryColour))))
						.binding(Colour.Colours.Original,
								() -> Colour.colourProfile,
								newValue -> Colour.colourProfile = newValue)
						.controller(opt -> new CyclingListController<Colour.Colours>(opt, List.of(Colour.Colours.values())))
						.build())
				/*.option(Option.createBuilder(boolean.class)
						.name(Text.literal("Better Math"))
						.tooltip(Text.literal("Better Math optimizes the games math to make it more efficient. To toggle it you must add the following JVM flag: ")
								.append(Text.literal("-Daaronmod.useBetterMath=<true or false>").styled(style -> style.withColor(colourProfile.primaryColour))))
						.binding(false,
								() -> Main.USE_BETTER_MATH,
								newValue -> placeholderBoolean = newValue)
						.controller(BooleanController::new)
						.available(false)
						.build())*/
				.option(Option.createBuilder(boolean.class)
						.name(Text.literal("Math FMA"))
						.tooltip(Text.literal("If your CPU supports it, you can enable FMA which minimizes the CPU time spent on some math calculations; you can enable it by adding this JVM flag: ")
								.append(Text.literal("-Djoml.useMathFma").styled(style -> style.withColor(colourProfile.primaryColour)))
								.append((Main.SUPPORTS_FMA ? Text.literal("\n\nThis CPU supports FMA!").styled(style -> style.withColor(0x1a7f37)) : Text.literal("\n\nThis CPU doesn't support FMA!").styled(style -> style.withColor(0xcf222e))))
								.append(Text.literal("\n\n\u26a0 Warning: Enabling FMA on an unsupported CPU can result in a significant performance hit!").styled(style -> style.withColor(0xeac864))))
						.binding(false,
								() -> Options.USE_MATH_FMA,
								newValue -> placeholderBoolean = newValue)
						.controller(BooleanController::new)
						.available(false)
						.build())
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Display"))
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Shadowed Scoreboard Text"))
								.tooltip(Text.literal("Adds text shadowing to the scoreboard!"))
								.binding(true,
										() -> shadowedScoreboard,
										newValue -> shadowedScoreboard = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Shadowed Nametag Text"))
								.tooltip(Text.literal("Adds text shadowing to nametags!"))
								.binding(true,
										() -> shadowedNametags,
										newValue -> shadowedNametags = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("FPS Display"))
								.tooltip(Text.literal("Displays your FPS in the top left corner of your screen!"))
								.binding(true,
										() -> fpsDisplay,
										newValue -> fpsDisplay = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Extra Debug Info"))
								.tooltip(Text.literal("Adds some extra information to the F3 menu."))
								.binding(true, 
										() -> extraDebugInfo, 
										newValue -> extraDebugInfo = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Hide Fire Overlay"))
								.tooltip(Text.literal("Prevents the fire overlay from being seen while you're burning."))
								.binding(false,
										() -> hideFireOverlay, 
										newValue -> hideFireOverlay = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Hide Mob Spawner Animations"))
								.tooltip(Text.literal("Hides the spinning mob inside of mob spawners aswell as also hiding the particles emitted by mob spawners.\n\nCan be useful in areas with lots of mob spawners!"))
								.binding(false,
										() -> hideSpinningMobInMobSpawner,
										newValue -> hideSpinningMobInMobSpawner = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Correct Ambient Darkness"))
								.tooltip(Text.literal("Fixes an issue causing overlays, vignettes and shadows to render as if it was always daytime. (MC-259651)"))
								.binding(true,
										() -> correctAmbientDarkness,
										newValue -> correctAmbientDarkness = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(double.class)
								.name(Text.literal("Zoom Multiplier"))
								.tooltip(Text.literal("Modifies how much your screen will zoom in by when using the zoom feature."))
								.binding(0.30d,
										() -> zoomMultiplier,
										newValue -> zoomMultiplier = newValue)
								.controller(opt -> new DoubleSliderController(opt, 0.15d, 0.45d, 0.01d))
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Old Message Trust Indicator Colours"))
								.tooltip(Text.literal("Changes the colour used for the ")
										.append(Text.literal("Not Secure").styled(style -> style.withColor(0xe84f58)))
										.append(Text.literal(" and "))
										.append(Text.literal("Modified").styled(style -> style.withColor(0xeac864)))
										.append(Text.literal(" trust indicators to what they were in 1.19.2 for better visual distinction.")))
								.binding(false,
										() -> oldMessageIndicatorColours, 
										newValue -> oldMessageIndicatorColours = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Potion Enchantment Glint"))
								.tooltip(Text.literal("Applies the enchantment glint to potions."))
								.binding(false,
										() -> shinyPotions,
										newValue -> shinyPotions = newValue)
								.controller(BooleanController::new)
								.build())
						.build())
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Functional"))
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Don't Stop Sounds on World Change"))
								.tooltip(Text.literal("Prevents sounds/music from being stopped upon changing worlds or exiting to the title screen."))
								.binding(false,
										() -> stopSoundsOnWorldChange,
										newValue -> stopSoundsOnWorldChange = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Copy Chat Messages"))
								.tooltip(Text.literal("Allows you to copy a chat message by middle clicking on it!"))
								.binding(true,
										() -> copyChatMessages,
										newValue -> copyChatMessages = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(CopyChatMode.class)
								.name(Text.literal("Copy Chat Mode"))
								.tooltip(Text.literal("The mod offers two different modes when copying chat messages:")
										.append(Text.literal("\n\nEntire Message: Copies the entire chat message."))
										.append(Text.literal("\n\nSingle Line: Copy chat messages line by line.")))
								.binding(CopyChatMode.ENTIRE_MESSAGE,
										() -> copyChatMode,
										newValue -> copyChatMode = newValue)
								.controller(opt -> new CyclingListController<CopyChatMode>(opt, List.of(CopyChatMode.values()), entry -> Text.literal(Functions.titleCase(entry.name().replace('_', ' ')))))
								.available(copyChatMessages)
								.build())
						.option(Option.createBuilder(int.class)
								.name(Text.literal("Chat History Length"))
								.tooltip(Text.literal("Change the maximum length of your chat history so that you don't miss any messages!"))
								.binding(100,
										() -> chatHistoryLength,
										newValue -> chatHistoryLength = newValue)
								.controller(opt -> new IntegerSliderController(opt, 100, 1000, 10))
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Infinite Hotbar Scrolling"))
								.tooltip(Text.literal("When scrolling in the hotbar with this disabled, it prevents you from scrolling down to slot 9 from slot 1, and from scrolling up to slot 1 from slot 9."))
								.binding(true,
										() -> infiniteHotbarScrolling,
										newValue -> infiniteHotbarScrolling = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Show Item Groups Outside of Creative"))
								.tooltip(Text.literal("When enabled, item groups are appended to the lore of an item even when you're outside of creative."))
								.binding(false,
										() -> showItemGroupsOutsideOfCreative,
										newValue -> showItemGroupsOutsideOfCreative = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Don't Reset Cursor Position"))
								.tooltip(Text.literal("When enabled, the position of your cursor doesn't reset between chest GUIs."))
								.binding(false,
										() -> resetCursorPosition,
										newValue -> resetCursorPosition = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Mac Only - Alt. FN+F3+N Keybind"))
								.tooltip(Text.literal("Adds FN+F3+J as an alternate keybind for FN+F3+N"))
								.binding(false,
										() -> alternateF3PlusNKey,
										newValue -> alternateF3PlusNKey = newValue)
								.controller(BooleanController::new)
								.available(MinecraftClient.IS_SYSTEM_MAC)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("December Christmas Chests"))
								.tooltip(Text.literal("You'll be able to see the christmas chests for the entire month of december!"))
								.binding(false,
										() -> decemberChristmasChests,
										newValue -> decemberChristmasChests = newValue)
								.controller(BooleanController::new)
								.available(CALENDAR.get(Calendar.MONTH) + 1 == 12)
								.flag(OptionFlag.ASSET_RELOAD)
								.build())
						.build())
				.build())
		.category(ConfigCategory.createBuilder()
				.name(Text.literal("Hypixel"))
				.option(Option.createBuilder(String.class)
						.name(Text.literal("Hypixel Api Key"))
						.tooltip(Text.literal("Set your Hypixel Api Key! To get one login to Hypixel and type \"/api new\""))
						.binding("",
								() -> key, 
								newValue -> key = newValue)
						.controller(StringController::new)
						.build())
				.option(Option.createBuilder(DayAverage.class)
						.name(Text.literal("Price Day Average"))
						.tooltip(Text.literal("Changes the day price average used in /lbin."))
						.binding(DayAverage.THREE_DAY,
								() -> dayAverage,
								newValue -> dayAverage = newValue)
						.controller(opt -> new CyclingListController<DayAverage>(opt, List.of(DayAverage.values())))
						.build())
				.option(Option.createBuilder(boolean.class)
						.name(Text.literal("Rainbowify Max Enchants"))
						.tooltip(Text.literal("Changes the text colour of maximum level enchantments in an item's lore to be a pretty rainbow gradient!\n\nExample: ")
								.append(TextTransformer.rainbowify("Critical VII, Vampirism VI")))
						.binding(false,
								() -> rainbowifyMaxSkyblockEnchantments,
								newValue -> rainbowifyMaxSkyblockEnchantments = newValue)
						.controller(BooleanController::new)
						.build())
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Dungeons"))
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Party Finder Stats Lookup"))
								.tooltip(Text.literal("Automatically shows a player's dungeon stats when they join from party finder."))
								.binding(true,
										() -> dungeonFinderPersonStats,
										newValue -> dungeonFinderPersonStats = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Colourful Party Finder Notes"))
								.tooltip(Text.literal("Allows you to stylize your party finder notes by using colour formatting codes!"))
								.binding(false,
										() -> colourfulPartyFinderNotes,
										newValue -> colourfulPartyFinderNotes = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Dungeon Score Message"))
								.tooltip(Text.literal("Sends a message when 270 or 300 score is reached! Messages can be up to 244 characters in length."))
								.binding(true,
										() -> dungeonScoreMessage,
										newValue -> dungeonScoreMessage = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(String.class)
								.name(Text.literal("270 Score Message"))
								.binding("270 Score Reached!",
										() -> twoHundredSeventyScore,
										newValue -> twoHundredSeventyScore = newValue)
								.controller(StringController::new)
								.available(dungeonScoreMessage)
								.build())
						.option(Option.createBuilder(String.class)
								.name(Text.literal("300 Score Message"))
								.binding("300 Score Reached!",
										() -> threeHundredScore,
										newValue -> threeHundredScore = newValue)
								.controller(StringController::new)
								.available(dungeonScoreMessage)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Old Master Star Display"))
								.tooltip(Text.literal("Reverts the display of master stars in item names to how it used to be.\n\n")
										.append(TextTransformer.fromLegacy("Example: §dDark Claymore §6✪§6✪§6✪§6✪§6✪§c➎ §r→ §dDark Claymore §c✪✪✪✪✪")))
								.binding(false,
										() -> oldMasterStars,
										newValue -> oldMasterStars = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Fancy Diamond Heads"))
								.tooltip(Text.literal("Diamond Heads will look a bit ")
										.append(Text.literal("fancier").styled(style -> style.withItalic(true)))
										.append(Text.literal("!")))
								.binding(false,
										() -> fancyDiamondHeads,
										newValue -> fancyDiamondHeads = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Hide Tooltips In Click On Time"))
								.tooltip(Text.literal("Hides the tooltips inside click on time so that they don't get in your way!"))
								.binding(true,
										() -> hideClickOnTimeTooltips,
										newValue -> hideClickOnTimeTooltips = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("M7 Dragon Bounding Box"))
								.tooltip(Text.literal("Displays bounding boxes around the dragon statues in M7."))
								.binding(false,
										() -> masterModeF7DragonBoxes,
										newValue -> masterModeF7DragonBoxes = newValue)
								.controller(BooleanController::new)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.literal("Glowing M7 Dragons"))
								.tooltip(Text.literal("Adds a glowing outline to the dragons in M7, making them easier to spot!"))
								.binding(false,
										() -> glowingM7Dragons,
										newValue -> glowingM7Dragons = newValue)
								.controller(BooleanController::new)
								.build())
						.build())
				.build())
		
		.category(ConfigCategory.createBuilder()
				.name(Text.literal("Particles"))
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Ash Particles"))
						.tooltip(Text.literal("Ash particles naturally generate in soul sand valleys."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_ash.state,
								newValue -> Particles.ParticleConfig.minecraft_ash.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Block Marker Particles"))
						.tooltip(Text.literal("Block Marker particles are the particles you see for the light and barrier blocks for example."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_block_marker.state,
								newValue -> Particles.ParticleConfig.minecraft_block_marker.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Dripping Cherry Leaves"))
						.tooltip(Text.literal("The dripping leaves from cherry leaves."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_dripping_cherry_leaves.state,
								newValue -> Particles.ParticleConfig.minecraft_dripping_cherry_leaves.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Falling Cherry Leaves"))
						.tooltip(Text.literal("The falling leaf particles from cherry leaves."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_falling_cherry_leaves.state,
								newValue -> Particles.ParticleConfig.minecraft_falling_cherry_leaves.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Landing Cherry Leaves"))
						.tooltip(Text.literal("The landing leaves from cherry leaves."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_landing_cherry_leaves.state,
								newValue -> Particles.ParticleConfig.minecraft_landing_cherry_leaves.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Crit Particles"))
						.tooltip(Text.literal("These particles can be seen when a critical hit is dealt against an enemy."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_crit.state,
								newValue -> Particles.ParticleConfig.minecraft_crit.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Dust Particles"))
						.tooltip(Text.literal("Dust particles can come in any colour! One example of their usage is the dust emitted by redstone torches."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_dust.state,
								newValue -> Particles.ParticleConfig.minecraft_dust.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Entity Effect Particles"))
						.tooltip(Text.literal("The particles seen when an entity has an active potion effect."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_entity_effect.state,
								newValue -> Particles.ParticleConfig.minecraft_entity_effect.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Enchanted Hit Particles"))
						.tooltip(Text.literal("Enchanted Hit particles can be seen when dealing damage with a weapon thats enchanted."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_enchanted_hit.state,
								newValue -> Particles.ParticleConfig.minecraft_enchanted_hit.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Explosion Particles"))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_explosion.state,
								newValue -> Particles.ParticleConfig.minecraft_explosion.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Firework Particles"))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_firework.state,
								newValue -> Particles.ParticleConfig.minecraft_firework.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Flash Particles"))
						.tooltip(Text.literal("Flash particles are the flash of colour you see in the air when a firework explodes."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_flash.state,
								newValue -> Particles.ParticleConfig.minecraft_flash.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Rain Splash Particles"))
						.tooltip(Text.literal("The small splashes of water you see on the ground when it rains."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_rain.state,
								newValue -> Particles.ParticleConfig.minecraft_rain.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Air Spore Blossom Particles"))
						.tooltip(Text.literal("The particles that float around in the air near spore blossoms."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_spore_blossom_air.state,
								newValue -> Particles.ParticleConfig.minecraft_spore_blossom_air.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("Falling Spore Blossom Particles"))
						.tooltip(Text.literal("The particles that fall down beneath spore blossoms."))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_falling_spore_blossom.state,
								newValue -> Particles.ParticleConfig.minecraft_falling_spore_blossom.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.option(Option.createBuilder(Particles.State.class)
						.name(Text.literal("White Ash Particles"))
						.tooltip(Text.literal("White Ash can be frequently found in the Basalt Deltas!"))
						.binding(Particles.State.FULL,
								() -> Particles.ParticleConfig.minecraft_white_ash.state,
								newValue -> Particles.ParticleConfig.minecraft_white_ash.state = newValue)
						.controller(opt -> new CyclingListController<Particles.State>(opt, List.of(Particles.State.values()), entry -> Text.literal(Functions.titleCase(entry.name()))))
						.available(!Main.OPTIFABRIC_LOADED)
						.build())
				.build())
		.save(Config::save)
		.build()
		.generateScreen(parent);
	}
}

package net.azureaaron.mod.config;

import java.awt.Color;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.azureaaron.mod.Colour;
import net.azureaaron.mod.Particles;
import net.azureaaron.mod.utils.Functions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class AaronModConfig {
	@SerialEntry
	public int version = 1;
	
	@SerialEntry
	public Colour.ColourProfiles colourProfile = Colour.ColourProfiles.Original;
	
	@SerialEntry
	public CustomColourProfile customColourProfile = new CustomColourProfile();
	
	@SerialEntry
	public boolean shadowedScoreboard = true;
	
	@SerialEntry
	public boolean dungeonFinderPersonStats = true;
	
	@SerialEntry
	public DayAverage dayAverage = DayAverage.THREE_DAY;
	
	@SerialEntry
	public boolean alternateF3PlusNKey = false;
	
	@SerialEntry
	public boolean dungeonScoreMessage = false;
	
	@SerialEntry
	public String twoHundredSeventyScore = "270 Score Reached!";
	
	@SerialEntry
	public String threeHundredScore = "300 Score Reached!";
	
	@SerialEntry
	public boolean oldMasterStars = false;
	
	@SerialEntry
	public boolean stopSoundsOnWorldChange = false;
	
	@SerialEntry
	public boolean fancyDiamondHeads = false;
	
	@SerialEntry
	public boolean fpsDisplay = true;
	
	@SerialEntry
	public boolean copyChatMessages = true;
	
	@SerialEntry
	public boolean infiniteHotbarScrolling = true;
	
	@SerialEntry
	public boolean showItemGroupsOutsideOfCreative = false;
	
	@SerialEntry
	public boolean extraDebugInfo = true;
	
	@SerialEntry
	public boolean resetCursorPosition = false;
	
	@SerialEntry
	public boolean decemberChristmasChests = false;
	
	@SerialEntry
	public boolean masterModeF7DragonBoxes = false;
	
	@SerialEntry
	public boolean colourfulPartyFinderNotes = false;
	
	@SerialEntry
	public boolean hideFireOverlay = false;
	
	@SerialEntry
	public boolean hideClickOnTimeTooltips = true;
	
	@SerialEntry
	public boolean oldMessageIndicatorColours = false;
	
	@SerialEntry
	public int chatHistoryLength = 100;
	
	@SerialEntry
	public double zoomMultiplier = 0.30d;
	
	@SerialEntry
	public boolean correctAmbientDarkness = true;
	
	@SerialEntry
	public boolean shadowedNametags = true;
	
	@SerialEntry
	public boolean shinyPotions = false;
	
	@SerialEntry
	public CopyChatMode copyChatMode = CopyChatMode.ENTIRE_MESSAGE;
	
	@SerialEntry
	public boolean hideSpinningMobInMobSpawner = false;
	
	@SerialEntry
	public boolean rainbowifyMaxSkyblockEnchantments = false;
	
	@SerialEntry
	public boolean glowingM7Dragons = false;
	
	@SerialEntry
	public RainbowifyMode rainbowifyMode = RainbowifyMode.DYNAMIC;
	
	@SerialEntry
	public MouseButton copyChatMouseButton = MouseButton.MIDDLE;
	
	@SerialEntry
	public boolean fixTabTranslucency = true;
	
	@SerialEntry
	public boolean hideNametagBackground = false;
	
	@SerialEntry
	public boolean m7DragonSpawnTimers = false;
	
	@SerialEntry
	public boolean m7GyroWaypoints = false;
	
	@SerialEntry
	public boolean m7ShootWaypoints = false;
	
	@SerialEntry
	public boolean m7StackWaypoints = false;
	
	@SerialEntry
	public boolean visualTextReplacer = false;
	
	@SerialEntry
	public boolean imagePreview = true;
	
	@SerialEntry
	public boolean m7DragonHealth = false;
	
	@SerialEntry
	public boolean optimizedScreenshots = false;
	
	@SerialEntry
	public float imagePreviewScale = 1f;
	
	@SerialEntry
	public boolean silenceResourcePackLogSpam = true;
	
	@SerialEntry
	public boolean enableSkyblockCommands = true;
	
	@SerialEntry
	public boolean secureSkinDownloads = true;
	
	@SerialEntry
	public boolean hideScoreboardScore = false;
	
	@SerialEntry
	public boolean m7DragonSpawnNotifications = false;
	
	@SerialEntry
	public boolean separateInventoryGuiScale = false;
	
	@SerialEntry
	public int inventoryGuiScale = 0;

	@SerialEntry
	public boolean noFog = false;

	@SerialEntry
	public boolean alwaysShowDayInF3 = false;

	@SerialEntry
	public boolean hideTutorials = false;

	@SerialEntry
	public float chromaSpeed = 4f;

	@SerialEntry
	public float chromaSaturation = 0.75f;

	@SerialEntry
	public boolean hideLightning = false;

	@SerialEntry
	public boolean hideWorldLoadingScreen = false;

	@SerialEntry
	public float statusEffectBackgroundAlpha = 1f;

	@SerialEntry
	public boolean goodSkyblockEnchantments = true;

	@SerialEntry
	public Color goodSkyblockEnchantmentColour = new Color(Formatting.GOLD.getColorValue());

	@SerialEntry
	public Object2ObjectOpenHashMap<Identifier, Particles.State> particles = new Object2ObjectOpenHashMap<>();
	
	@SerialEntry 
	public Object2FloatOpenHashMap<Identifier> particleScaling = new Object2FloatOpenHashMap<>();

	@SerialEntry
	public Object2FloatOpenHashMap<Identifier> particleAlphas = new Object2FloatOpenHashMap<>();

	@SerialEntry
	public TextReplacer textReplacer = new TextReplacer();

	@SerialEntry
	public ItemModelCustomization itemModelCustomization = new ItemModelCustomization();

	public static class TextReplacer {
		@SerialEntry
		public Object2ObjectLinkedOpenHashMap<String, Text> textReplacements = new Object2ObjectLinkedOpenHashMap<>();
	}
	
	public static class CustomColourProfile {
		@SerialEntry
		public Color primaryColour = new Color(0xFFFFFF);
		
		@SerialEntry
		public Color secondaryColour = new Color(0xFFFFFF);
		
		@SerialEntry
		public Color infoColour = new Color(0xFFFFFF);
		
		@SerialEntry
		public Color highlightColour = new Color(0xFFFFFF);
		
		@SerialEntry
		public Color hoverColour = new Color(0xFFFFFF);
		
		@SerialEntry
		public Color supportingInfoColour = new Color(0xFFFFFF);
	}

	public static class ItemModelCustomization {
		@SerialEntry
		public boolean enableItemModelCustomization = true;

		@SerialEntry
		//This constant is wrongly named in yarn - its actually the swing duration
		public int swingDuration = LivingEntity.GLOWING_FLAG;

		@SerialEntry
		public boolean ignoreHaste = false;

		@SerialEntry
		public AbstractHand mainHand = new AbstractHand();

		@SerialEntry
		public AbstractHand offHand = new AbstractHand();

		public static class AbstractHand {
			@SerialEntry
			public float x = 0f;

			@SerialEntry
			public float y = 0f;

			@SerialEntry
			public float z = 0f;

			@SerialEntry
			public float scale = 1f;

			@SerialEntry
			public float xRotation = 0f;

			@SerialEntry
			public float yRotation = 0f;

			@SerialEntry
			public float zRotation = 0f;
		}
	}

	public enum DayAverage {
		ONE_DAY, 
		THREE_DAY, 
		SEVEN_DAY;
		
		@Override
		public String toString() {
			return Functions.titleCase(name().replace('_', ' '));
		}
	}

	public enum CopyChatMode {
		ENTIRE_MESSAGE,
		SINGLE_LINE;
		
		@Override
		public String toString() {
			return Functions.titleCase(name().replace('_', ' '));
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
			return Functions.titleCase(name()) + " Button";
		}
	}
}

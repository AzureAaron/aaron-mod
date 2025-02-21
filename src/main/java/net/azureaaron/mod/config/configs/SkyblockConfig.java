package net.azureaaron.mod.config.configs;

import java.awt.Color;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.azureaaron.mod.utils.Functions;
import net.minecraft.util.Formatting;

public class SkyblockConfig {
	@SerialEntry
	public Commands commands = new Commands();

	@SerialEntry
	public Enchantments enchantments = new Enchantments();

	@SerialEntry
	public Dungeons dungeons = new Dungeons();

	@SerialEntry
	public M7 m7 = new M7();

	public static class Commands {
		@SerialEntry
		public boolean enableSkyblockCommands = true;

		@SerialEntry
		public DayAverage lbinPriceDayAverage = DayAverage.THREE_DAY;
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

	public static class Enchantments {
		@SerialEntry
		public boolean rainbowMaxEnchants = true;

		@SerialEntry
		public RainbowMode rainbowMode = RainbowMode.CHROMA;

		@SerialEntry
		public boolean showGoodEnchants = true;

		@SerialEntry
		public Color goodEnchantsColour = new Color(Formatting.GOLD.getColorValue());
	}

	public enum RainbowMode {
		STATIC, CHROMA;

		@Override
		public String toString() {
			return switch (this) {
			case STATIC -> "Still";
			case CHROMA -> "Chroma";
			};
		}
	}

	public static class Dungeons {
		@SerialEntry
		public boolean dungeonFinderPlayerStats = true;

		@SerialEntry
		public boolean oldMasterStars = false;

		@SerialEntry
		public boolean fancyDiamondHeadNames = false;

		@SerialEntry
		public boolean hideClickOnTimeTooltips = true;
	}

	public static class M7 {
		@SerialEntry
		public boolean glowingDragons = true;

		@SerialEntry
		public boolean dragonBoundingBoxes = true;

		@SerialEntry
		public boolean dragonSpawnTimers = true;

		@SerialEntry
		public boolean dragonSpawnNotifications = true;

		@SerialEntry
		public boolean dragonHealthDisplay = true;

		@SerialEntry
		public boolean dragonAimWaypoints = false;

		@SerialEntry
		public boolean arrowStackWaypoints = false;
	}
}

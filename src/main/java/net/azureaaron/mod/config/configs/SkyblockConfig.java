package net.azureaaron.mod.config.configs;

import java.awt.Color;

import net.azureaaron.mod.utils.Functions;
import net.minecraft.util.Formatting;

public class SkyblockConfig {
	public Commands commands = new Commands();

	public Enchantments enchantments = new Enchantments();

	public Dungeons dungeons = new Dungeons();

	public M7 m7 = new M7();

	public static class Commands {
		public boolean enableSkyblockCommands = true;

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
		public boolean rainbowMaxEnchants = true;

		public RainbowMode rainbowMode = RainbowMode.CHROMA;

		public boolean showGoodEnchants = true;

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
		public boolean dungeonFinderPlayerStats = true;

		public boolean oldMasterStars = false;

		public boolean fancyDiamondHeadNames = false;

		public boolean hideClickOnTimeTooltips = true;
	}

	public static class M7 {
		public boolean glowingDragons = true;

		public boolean dragonBoundingBoxes = true;

		public boolean dragonSpawnTimers = true;

		public boolean dragonSpawnNotifications = true;

		public boolean dragonHealthDisplay = true;

		public boolean dragonAimWaypoints = false;

		public boolean arrowStackWaypoints = false;
	}
}

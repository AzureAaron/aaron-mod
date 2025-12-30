package net.azureaaron.mod.utils;

import java.awt.Color;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;

/**
 * Class containing various utility/helper functions.
 *
 * @author Aaron
 */
public class Functions {

	public static String titleCase(String string) {
		String[] split = string.toLowerCase(Locale.CANADA).split(" ");

		for (int i = 0; i < split.length; i++) {
			split[i] = String.valueOf(Character.toUpperCase(split[i].charAt(0))) + split[i].substring(1);
		}

		return String.join(" ", split);
	}

	public static String possessiveEnding(String string) {
		return string.endsWith("s") ? string + "'" : string + "'s";
	}

	public static boolean isOnHypixel() {
		String serverAddress = Cache.currentServerAddress;
		Minecraft client = Minecraft.getInstance();
		String serverBrand = client.player != null && client.player.connection != null && client.player.connection.serverBrand() != null ? client.player.connection.serverBrand() : "";

		return serverAddress.contains("hypixel.net") || serverAddress.contains("hypixel.io") || serverBrand.contains("Hypixel BungeeCord");
	}

	public static int romanToInt(String numeral) {
		return switch (numeral) {
			case "I" -> 1;
			case "II" -> 2;
			case "III" -> 3;
			case "IV" -> 4;
			case "V" -> 5;

			default -> 0;
		};
	}

	public static int hsbToRGB(float hue, float saturation, float value) {
		return Color.getHSBColor(hue, saturation, value).getRGB();
	}

	public static boolean isInSkyblock() {
		return Utils.isOnSkyblock();
	}

	public static boolean isUuid(String uuid) {
		return uuid.length() == 36 || uuid.length() == 32;
	}

	public static void runIf(Runnable task, BooleanSupplier condition) {
		if (condition.getAsBoolean()) task.run();
	}
}

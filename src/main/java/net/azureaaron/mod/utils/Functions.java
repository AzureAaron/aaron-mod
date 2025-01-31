package net.azureaaron.mod.utils;

import java.awt.Color;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.client.MinecraftClient;

/**
 * Class containing various utility/helper functions.
 * 
 * @author Aaron
 */
public class Functions {	
	/** {@link Object2LongLinkedOpenHashMap} containing time units and their equivalent in milliseconds. */
	private static final Object2LongLinkedOpenHashMap<String> TIMES = new Object2LongLinkedOpenHashMap<>();

	static {
		TIMES.put("year", TimeUnit.DAYS.toMillis(365));
		TIMES.put("month", TimeUnit.DAYS.toMillis(30));
		TIMES.put("week", TimeUnit.DAYS.toMillis(7));
		TIMES.put("day", TimeUnit.DAYS.toMillis(1));
		TIMES.put("hour", TimeUnit.HOURS.toMillis(1));
		TIMES.put("minute", TimeUnit.MINUTES.toMillis(1));
		TIMES.put("second", TimeUnit.SECONDS.toMillis(1));
	}

	/**
	 * Relative Time Ago - From https://memorynotfound.com/calculate-relative-time-time-ago-java/
	 */
	public static String toRelative(long duration, int maxLevel) {
		StringBuilder res = new StringBuilder();
		int level = 0;
		for (Object2LongMap.Entry<String> time : TIMES.object2LongEntrySet()){
			long timeDelta = duration / time.getLongValue();
			if (timeDelta > 0){
				res.append(timeDelta)
				.append(" ")
				.append(time.getKey())
				.append(timeDelta > 1 ? "s" : "")
				.append(", ");
				duration -= time.getLongValue() * timeDelta;
				level++;
			}
			if (level == maxLevel){
				break;
			}
		}
		if ("".equals(res.toString())) {
			return "0 seconds ago";
		} else {
			res.setLength(res.length() - 2);
			res.append(" ago");
			return res.toString();
		}
	}

	public static String toRelative(long duration) {
		return toRelative(duration, TIMES.size());
	}

	public static String toMostRelative(long duration) {
		return toRelative(duration).split(",")[0].replace(" ago", "") + " ago";
	}

	public static String titleCase(String string) {
		String[] split = string.toLowerCase().split(" ");

		for(int i = 0; i < split.length; i++) {
			split[i] = String.valueOf(Character.toUpperCase(split[i].charAt(0))) + split[i].substring(1);	
		}

		return String.join(" ", split);
	}

	public static String possessiveEnding(String string) {
		return string.endsWith("s") ? string + "'" : string + "'s";
	}

	public static boolean isOnHypixel() {
		String serverAddress = Cache.currentServerAddress;
		MinecraftClient client = MinecraftClient.getInstance();
		String serverBrand = client.player != null && client.player.networkHandler != null && client.player.networkHandler.getBrand() != null ? client.player.networkHandler.getBrand() : "";

		return serverAddress.contains("hypixel.net") || serverAddress.contains("hypixel.io") || serverBrand.contains("Hypixel BungeeCord");
	}

	public static int romanToInt(String numeral) {
		return switch(numeral) {
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

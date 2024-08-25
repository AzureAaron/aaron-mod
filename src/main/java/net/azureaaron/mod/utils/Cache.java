package net.azureaaron.mod.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.util.Formatting;

/**
 * This class is used to cache & save values that are related to the mod's operations.<br><br>
 * It also contains some methods related to caching values as well.
 * 
 * @author Aaron
 */
public class Cache {
	@Deprecated(forRemoval = true)
	public static int powerBlessing = 0;
	@Deprecated(forRemoval = true)
	public static int wisdomBlessing = 0;
	@Deprecated(forRemoval = true)
	public static int lifeBlessing = 0;
	@Deprecated(forRemoval = true)
	public static int stoneBlessing = 0;
	@Deprecated(forRemoval = true)
	public static boolean timeBlessing = false;
	
	@Deprecated(forRemoval = true)
	public static long lastTwoHundredSeventyScore = 0L;
	@Deprecated(forRemoval = true)
	public static long lastThreeHundredScore = 0L;	
	public static boolean inM7Phase5 = false;
	@Deprecated(forRemoval = true)
	public static volatile boolean inDungeonBossRoom = false;
	@Deprecated(forRemoval = true)
	public static int currentScore = 0;

	public static String currentServerAddress = "";
	public static String lastServerAddress = "";

	public static void incrementBlessing(String blessing, String level) {
		switch(blessing) {
			case "Power" -> powerBlessing += Functions.romanToInt(level);
			case "Wisdom" -> wisdomBlessing += Functions.romanToInt(level);
			case "Life" -> lifeBlessing += Functions.romanToInt(level);
			case "Stone" -> stoneBlessing += Functions.romanToInt(level);
			case "Time" -> timeBlessing = true;
		}
	}
	
	public static void resetBlessings() {
		powerBlessing = 0;
		wisdomBlessing = 0;
		lifeBlessing = 0;
		stoneBlessing = 0;
		timeBlessing = false;
	}
	
	/**{@link HashSet} containing bazaar product names. Example: {@code Fifth Master Star}*/
	public static final HashSet<String> PRODUCTS_LIST = new HashSet<>();
	
	/**{@link HashMap} used for mapping product names to skyblock item ids. Example: {@code Fifth Master Star → FIFTH_MASTER_STAR}*/
	public static final HashMap<String, String> PRODUCTS_MAP = new HashMap<>();
	
	/**{@link HashSet} containing a list of item names. Example: {@code Astraea}*/
	public static final HashSet<String> ITEMS_LIST = new HashSet<>();
	
	/**{@link HashMap} used for mapping item names to skyblock item ids. Example: {@code Astraea → ASTRAEA}*/
	public static final HashMap<String, String> ITEM_NAMES = new HashMap<>();
	
	public static void populate(JsonArray itemsData) {
		//HashMap used for mapping item ids to item names
		HashMap<String, String> items = new HashMap<>();
		
		CompletableFuture.supplyAsync(() -> {
			//Populate skyblock items cache
			try {				
				for (JsonElement item : itemsData) {
					String itemName = Formatting.strip(item.getAsJsonObject().get("name").getAsString());
					String itemId = item.getAsJsonObject().get("id").getAsString();

					//Exclude items that aren't salable
					if (itemName.contains("Minion") || item.getAsJsonObject().get("soulbound") != null) continue;
					
					ITEMS_LIST.add(itemName);
					ITEM_NAMES.put(itemName, itemId);
					items.put(itemId, itemName);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}).thenAccept(result -> {
			//Populate bazaar products cache
			if (!result) return; //Prevent exception (crash?) if item cache doesn't get populated

			try {
				String bazaarResponse = Http.sendGetRequest("https://api.hypixel.net/v2/skyblock/bazaar");
				JsonObject bazaarData = JsonParser.parseString(bazaarResponse).getAsJsonObject();

				bazaarData.get("products").getAsJsonObject().keySet().forEach(key -> {
					if (key.startsWith("ENCHANTMENT") || key.startsWith("ESSENCE")) {
						String itemName = Functions.titleCase(key
								.replaceAll("ENCHANTMENT_ULTIMATE_", "")
								.replaceAll("ENCHANTMENT_", "")
								.replaceAll("ESSENCE_", "")
								.replaceAll("REITERATE", "DUPLEX") //So that I don't get a "bug" report about this!!
								.replaceAll("_", " "));

						switch (key) {
							case String s when s.startsWith("ESSENCE") -> itemName += " Essence";
							case String s when s.startsWith("ENCHANTMENT_ULTIMATE_WISE") -> itemName = "Ultimate " + itemName;

							default -> {}
						}
						
						PRODUCTS_LIST.add(itemName);
						PRODUCTS_MAP.put(itemName, key);
					} else {
						String itemName = items.get(key);

						if (itemName != null) {
							PRODUCTS_LIST.add(itemName);
							ITEMS_LIST.remove(itemName);
							PRODUCTS_MAP.put(itemName, key);
						} else { //Handles the ghost items in the bazaar api
							PRODUCTS_LIST.add(key);
							PRODUCTS_MAP.put(key, key);
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}

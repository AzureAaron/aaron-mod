package net.azureaaron.mod.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

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
	
	public static int powerBlessing = 0;
	public static int wisdomBlessing = 0;
	public static int lifeBlessing = 0;
	public static int stoneBlessing = 0;
	public static boolean timeBlessing = false;
	
	public static long lastTwoHundredSeventyScore = 0L;
	public static long lastThreeHundredScore = 0L;	
	public static boolean inM7Phase5 = false;
	public static int currentScore = 0;
	
	public static String currentServerAddress = "";
	public static String lastServerAddress = "";
	
	/**@implNote It isn't possible to find out which exact player trigger a shrieker so this may not be 100% accurate*/
	public static int warningLevel = 0;
	public static long lastShriekTime = 0L;
			
	static {
		populate();
	}
	
	public static void incrementBlessing(String blessing, String level) {
		switch(blessing) {
		case "Power": 
			powerBlessing += Functions.romanToInt(level);
			break;
		case "Wisdom":
			wisdomBlessing += Functions.romanToInt(level);
			break;
		case "Life":
			lifeBlessing += Functions.romanToInt(level);
			break;
		case "Stone":
			stoneBlessing += Functions.romanToInt(level);
			break;
		case "Time":
			timeBlessing = true;
			break;
		}
		return;
	}
	
	public static void resetBlessings() {
		powerBlessing = 0;
		wisdomBlessing = 0;
		lifeBlessing = 0;
		stoneBlessing = 0;
		timeBlessing = false;
	}
	
	public static int relativeWarningLevel() {
		int tenMinuteIncrementsPassed = (int) ((System.currentTimeMillis() - lastShriekTime) / 600000L);
		return lastShriekTime == 0L ? 0 : Math.max(warningLevel - tenMinuteIncrementsPassed, 0);
	}
	
	/**{@link HashSet} containing bazaar product names. Example: {@code Fifth Master Star}*/
	public static final HashSet<String> PRODUCTS_LIST = new HashSet<>();
	
	/**{@link HashMap} used for mapping product names to skyblock item ids. Example: {@code Fifth Master Star → FIFTH_MASTER_STAR}*/
	public static final HashMap<String, String> PRODUCTS_MAP = new HashMap<>();
	
	/**{@link HashSet} containing a list of item names. Example: {@code Astraea}*/
	public static final HashSet<String> ITEMS_LIST = new HashSet<>();
	
	/**{@link HashMap} used for mapping item names to skyblock item ids. Example: {@code Astraea → ASTRAEA}*/
	public static final HashMap<String, String> ITEM_NAMES = new HashMap<>();
	
	public static void populate() {
		//HashMap used for mapping item ids to item names
		HashMap<String, String> items = new HashMap<>();
		
		CompletableFuture.supplyAsync(() -> {
			//Populate skyblock items cache
			try {
				String itemsResponse = Http.sendHypixelRequest("resources/skyblock/items", "", false, false);
				JsonObject itemsData = JsonParser.parseString(itemsResponse).getAsJsonObject();
				
				for(JsonElement item : itemsData.get("items").getAsJsonArray()) {
					String itemName = Formatting.strip(item.getAsJsonObject().get("name").getAsString());
					String itemId = item.getAsJsonObject().get("id").getAsString();
					
					//Exclude items that aren't salable
					if(itemName.contains("Minion")) continue;
					if(item.getAsJsonObject().get("soulbound") != null) continue;
					
					ITEMS_LIST.add(itemName);
					ITEM_NAMES.put(itemName, itemId);
					items.put(itemId, itemName);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}).thenApply(result -> {
			//Populate bazaar products cache
			if(!result) return false; //Prevent exception (crash?) if item cache doesn't get populated
			try {
				String bazaarResponse = Http.sendHypixelRequest("skyblock/bazaar", "", false, false);
				JsonObject bazaarData = JsonParser.parseString(bazaarResponse).getAsJsonObject();
				
				bazaarData.get("products").getAsJsonObject().keySet().forEach(key -> {
					if(key.startsWith("ENCHANTMENT") || key.startsWith("ESSENCE")) {
						String itemName = key;
						itemName = itemName.replaceAll("ENCHANTMENT_ULTIMATE_", "");
						itemName = itemName.replaceAll("ENCHANTMENT_", "");
						itemName = itemName.replaceAll("ESSENCE_", "");
						itemName = itemName.replaceAll("REITERATE", "DUPLEX"); //So that I don't get a "bug" report about this!!
						itemName = itemName.replaceAll("_", " ");
						itemName = Functions.titleCase(itemName);
						if(key.startsWith("ESSENCE")) itemName += " Essence";
						if(key.startsWith("ENCHANTMENT_ULTIMATE_WISE")) itemName = "Ultimate " + itemName;
						
						PRODUCTS_LIST.add(itemName);
						PRODUCTS_MAP.put(itemName, key);
					} else {
						String itemName = items.get(key);
						if(itemName != null) {
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
			return null;
		});
	}
}

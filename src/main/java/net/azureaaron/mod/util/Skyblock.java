package net.azureaaron.mod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.azureaaron.mod.commands.NetworthCommand;

public class Skyblock {
	public static final String[] MAX_LEVEL_SKYBLOCK_ENCHANTMENTS = {/* Armour */ "Aqua Affinity I", "Big Brain V", "Blast Protection VII", 
			"Counter-Strike V", "Depth Strider III", "Feather Falling X", "Feather Falling XX", "Ferocious Mana X", "Fire Protection VII",
			"Frost Walker II", "Growth VII", "Hardened Mana X", "Hecatomb X", "Mana Vampire X", "Projectile Protection VII", "Protection VII",
			"Rejuvenate V", "Respiration III", "Respite V", "Smarty Pants V", "Strong Mana X", "Sugar Rush III", "Thorns III", 
			"True Protection I", /* Weapons */ "Bane of Arthropods VII", "Champion X", "Chance V", "Cleave VI", "Critical VII", "Cubism VI",
			"Divine Gift III", "Dragon Hunter V", "Dragon Tracer V", "Ender Slayer VII", "Execute VI", "Fire Aspect III", "First Strike V",
			"Flame II", "Giant Killer VII", "Impaling III", "Infinite Quiver X", "Knockback II", "Lethality VI", "Life Steal V", "Looting V",
			"Luck VII", "Mana Steal III", "Overload V", "Piercing I", "Power VII", "Prosecute VI", "Punch II", "Scavenger V",
			"Sharpness VII", "Smite VII", "Smoldering V", "Snipe IV", "Syphon V", "Tabasco III", "Thunderbolt VI", "Thunderlord VII",
			"Titan Killer VII", "Triple-Strike V", "Vampirism VI", "Venomous VI", "Vicious V", /* Fishing */ "Angler VI", "Blessing VI",
			"Caster VI", "Charm V", "Corruption V", "Expertise X", "Frail VI", "Luck of the Sea VI", "Lure VI", "Magnet VI", "Piscary VI",
			"Spiked Hook VI", /* Farming Specific */ "Cultivating X", "Delicate V", "Harvesting VI", "Replenish I", "Sunder VI",
			"Turbo-Cacti V", "Turbo-Cane V", "Turbo-Carrot V", "Turbo-Cocoa V", "Turbo-Melon V", "Turbo-Mushrooms V", "Turbo-Potato V", 
			"Turbo-Pumpkin V", "Turbo-Warts V", "Turbo-Wheat V", /* Axes/Pickaxes */ "Compact X", "Efficiency VI", "Efficiency X",
			"Fortune IV", "Pristine V", "Silk Touch I", "Smelting Touch I", /* Equipment */ "Cayenne V", /* Misc/Multipurpose */ 
			"Experience V", "Rainbow I" };
	
	public static JsonObject getSelectedProfile2(String profiles) {
		if(profiles == null) return null;
		JsonObject skyblockData = JsonParser.parseString(profiles).getAsJsonObject();
		JsonArray profilesArray = skyblockData.get("profiles").getAsJsonArray();
		for(JsonElement profile : profilesArray) {
			JsonObject iteratedProfile = profile.getAsJsonObject();
			if(iteratedProfile.get("selected").getAsBoolean() == true) return iteratedProfile;
		}
		return null;
	}
	
	public static NetworthCommand.Networth readNetworthData(String data, long bank, long purse) {
		if(data == null) return null;
		JsonObject json = JsonParser.parseString(data).getAsJsonObject();
		JsonObject networthData = json.get("data").getAsJsonObject().get("categories").getAsJsonObject();
		
		long accessoriesValue = (networthData.get("talismans") != null) ? networthData.get("talismans").getAsJsonObject().get("total").getAsLong() : 0L;
		long armourValue = (networthData.get("armor") != null) ? networthData.get("armor").getAsJsonObject().get("total").getAsLong() : 0L;
		long enderchestValue = (networthData.get("enderchest") != null) ? networthData.get("enderchest").getAsJsonObject().get("total").getAsLong() : 0L;
		long inventoryValue = (networthData.get("inventory") != null) ? networthData.get("inventory").getAsJsonObject().get("total").getAsLong() : 0L;
		long petsValue = (networthData.get("pets") != null) ? networthData.get("pets").getAsJsonObject().get("total").getAsLong() : 0L;
		long sacksValue = (json.get("data").getAsJsonObject().get("sacks") != null) ? json.get("data").getAsJsonObject().get("sacks").getAsLong() : 0L;
		long storageValue = (networthData.get("storage") != null) ? networthData.get("storage").getAsJsonObject().get("total").getAsLong() : 0L;
		long wardrobeValue = (networthData.get("wardrobe_inventory") != null) ? networthData.get("wardrobe_inventory").getAsJsonObject().get("total").getAsLong() : 0L;
		
		long overallValue = accessoriesValue + armourValue + bank + enderchestValue + inventoryValue + petsValue + purse + sacksValue + storageValue + wardrobeValue;
		
		return new NetworthCommand.Networth(
				accessoriesValue,
				armourValue,
				bank,
				enderchestValue,
				inventoryValue,
				overallValue,
				petsValue,
				purse,
				sacksValue,
				storageValue,
				wardrobeValue
				);
	}
	
	public static String getDojoGrade(int score) {
		if(score == 0) return "None";
		if(score >= 1000) return "S";
		if(score >= 800) return "A";
		if(score >= 600) return "B";
		if(score >= 400) return "C";
		if(score >= 200) return "D";
		if(score < 200) return "F";
		return "UNKNOWN";
	}
}

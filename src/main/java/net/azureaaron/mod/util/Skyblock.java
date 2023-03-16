package net.azureaaron.mod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.azureaaron.mod.commands.NetworthCommand;

public class Skyblock {
	
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

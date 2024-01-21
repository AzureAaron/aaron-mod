package net.azureaaron.mod.util;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.azureaaron.mod.commands.NetworthCommand;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Skyblock {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Codec<Map<String, ItemStack>> RARE_LOOT_CODEC = Codec.unboundedMap(Codec.STRING, ItemStack.CODEC);
	private static final Identifier RARE_LOOT = new Identifier("aaron-mod", "skyblock/rare_loot_items.json");
	
	public static final String[] MAX_LEVEL_SKYBLOCK_ENCHANTMENTS = {/* Armour */ "Aqua Affinity I", "Big Brain V", "Blast Protection VII", 
			"Counter-Strike V", "Depth Strider III", "Feather Falling X", "Feather Falling XX", "Ferocious Mana X", "Fire Protection VII",
			"Frost Walker II", "Growth VII", "Hardened Mana X", "Hecatomb X", "Mana Vampire X", "Pesterminator V", "Projectile Protection VII", "Protection VII",
			"Reflection V", "Rejuvenate V", "Respiration III", "Respiration IV", "Respite V", "Smarty Pants V", "Strong Mana X", "Sugar Rush III", "Thorns III", 
			"True Protection I", /* Weapons */ "Bane of Arthropods VII", "Champion X", "Chance V", "Cleave VI", "Critical VII", "Cubism VI",
			"Divine Gift III", "Dragon Hunter V", "Dragon Tracer V", "Ender Slayer VII", "Execute VI", "Fire Aspect III", "First Strike V",
			"Flame II", "Giant Killer VII", "Impaling III", "Infinite Quiver X", "Knockback II", "Lethality VI", "Life Steal V", "Looting V",
			"Luck VII", "Mana Steal III", "Overload V", "Piercing I", "Power VII", "Prosecute VI", "Punch II", "Scavenger V",
			"Sharpness X", "Sharpness VII", "Smite VII", "Smoldering V", "Snipe IV", "Syphon V", "Tabasco III", "Thunderbolt VI", "Thunderlord VII",
			"Titan Killer VII", "Transylvanian V", "Triple-Strike V", "Vampirism VI", "Venomous VI", "Vicious V", /* Fishing */ "Angler VI", "Blessing VI",
			"Caster VI", "Charm V", "Corruption V", "Expertise X", "Frail VI", "Luck of the Sea VI", "Lure VI", "Magnet VI", "Piscary VI",
			"Spiked Hook VI", /* Farming Specific */ "Cultivating X", "Dedication IV", "Delicate V", "Green Thumb V", "Harvesting VI", 
			"Replenish I", "Sunder VI", "Turbo-Cacti V", "Turbo-Cane V", "Turbo-Carrot V", "Turbo-Cocoa V", "Turbo-Melon V", 
			"Turbo-Mushrooms V", "Turbo-Potato V", "Turbo-Pumpkin V", "Turbo-Warts V", "Turbo-Wheat V", 
			/* Axes/Pickaxes */ "Compact X", "Efficiency VI", "Efficiency X", "Fortune IV", "Pristine V", "Silk Touch I", "Smelting Touch I", 
			/* Equipment */ "Cayenne V", "Prosperity V", "Quantum V", /* Misc/Multipurpose */ "Experience V", "Rainbow III" };
	
	public static final Map<String, ItemStack> RARE_LOOT_ITEMS = new HashMap<>();
	
	public static void init() {
		ClientLifecycleEvents.CLIENT_STARTED.register(Skyblock::loadRareLootItems);
	}
	
	private static void loadRareLootItems(MinecraftClient client) {
		CompletableFuture.supplyAsync(() -> {
			try (BufferedReader reader = client.getResourceManager().openAsReader(RARE_LOOT)) {
				return RARE_LOOT_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).result().orElseThrow();
			} catch (Exception e) {
				LOGGER.error("[Aaron's Mod] Failed to load rare loot items file!", e);
				
				return Map.<String, ItemStack>of();
			}
		}).thenAccept(RARE_LOOT_ITEMS::putAll);
	}
	
	public static JsonObject getSelectedProfile2(String profiles) throws IllegalStateException {
		if (profiles == null) return null;
		
		JsonObject skyblockData = JsonParser.parseString(profiles).getAsJsonObject();
		JsonArray profilesArray = skyblockData.getAsJsonArray("profiles");
		
		if (profilesArray == null) throw new IllegalStateException(Messages.PROFILES_NOT_MIGRATED_ERROR.get().getString()); //If the player's profile hasn't been migrated
		
		for (JsonElement profile : profilesArray) {
			JsonObject iteratedProfile = profile.getAsJsonObject();
			if (iteratedProfile.get("selected").getAsBoolean() == true) return iteratedProfile;
		}
		
		throw new IllegalStateException(Messages.PROFILES_NOT_MIGRATED_ERROR.get().getString()); //After the migration players can apparently have no selected profile
	}
	
	public static boolean isInventoryApiEnabled(JsonObject inventoryData) {
		return inventoryData != null && inventoryData.has("inv_contents");
	}
	
	public static boolean isSkillsApiEnabled(JsonObject profile) {
		return profile.getAsJsonObject("player_data").has("experience");
	}
	
	public static NetworthCommand.Networth readNetworthData(String data, long bank, long purse) {
		if (data == null) return null;
		JsonObject json = JsonParser.parseString(data).getAsJsonObject();
		JsonObject networthData = json.getAsJsonObject("data").getAsJsonObject("categories");
		
		long accessoriesValue = JsonHelper.getLong(networthData, "talismans.total").orElse(0L);
		long armourValue = JsonHelper.getLong(networthData, "armor.total").orElse(0L);
		long enderchestValue = JsonHelper.getLong(networthData, "enderchest.total").orElse(0L);
		long inventoryValue = JsonHelper.getLong(networthData, "inventory.total").orElse(0L);
		long petsValue = JsonHelper.getLong(networthData, "pets.total").orElse(0L);
		long sacksValue = JsonHelper.getLong(json, "data.sacks").orElse(0L);
		long storageValue = JsonHelper.getLong(networthData, "storage.total").orElse(0L);
		long wardrobeValue = JsonHelper.getLong(networthData, "wardrobe_inventory.total").orElse(0L);
		
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
		if (score == 0) return "None";
		if (score >= 1000) return "S";
		if (score >= 800) return "A";
		if (score >= 600) return "B";
		if (score >= 400) return "C";
		if (score >= 200) return "D";
		if (score < 200) return "F";
		
		return "UNKNOWN";
	}
	
	public static int calculateProfileSocialXp(JsonObject profile) {
		int socialXp = 0;
		JsonObject members = profile.getAsJsonObject("members");
		
		for (String uuid : members.keySet()) {
			JsonObject member = members.getAsJsonObject(uuid);
			socialXp += JsonHelper.getInt(member, "player_data.experience.SKILL_SOCIAL").orElse(0);
		}
		
		return socialXp;
	}
}

package net.azureaaron.mod.util;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import net.azureaaron.mod.Main;
import net.azureaaron.mod.commands.NetworthCommand;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Skyblock {
	private static final Logger LOGGER = LogUtils.getLogger();
	
	private static final Codec<Map<String, ItemStack>> RARE_LOOT_CODEC = Codec.unboundedMap(Codec.STRING, ItemStack.CODEC);
	private static final Identifier RARE_LOOT = new Identifier(Main.NAMESPACE, "skyblock/rare_loot_items.json");
	public static final Map<String, ItemStack> RARE_LOOT_ITEMS = new HashMap<>();
	
	private static final Codec<List<String>> MAX_ENCHANTMENTS_CODEC = Codec.list(Codec.STRING);
	private static final Identifier MAX_ENCHANTMENTS = new Identifier(Main.NAMESPACE, "skyblock/max_enchantments.json");
	public static final List<String> MAX_LEVEL_ENCHANTMENTS = new ArrayList<>();
		
	public static void init() {
		ClientLifecycleEvents.CLIENT_STARTED.register(Skyblock::loadRareLootItems);
		ClientLifecycleEvents.CLIENT_STARTED.register(Skyblock::loadMaxEnchants);
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
	
	private static void loadMaxEnchants(MinecraftClient client) {
		CompletableFuture.supplyAsync(() -> {
			try (BufferedReader reader = client.getResourceManager().openAsReader(MAX_ENCHANTMENTS)) {
				return MAX_ENCHANTMENTS_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).result().orElseThrow();
			} catch (Exception e) {
				LOGGER.error("[Aaron's Mod] Failed to load max enchantments file!", e);
				
				return List.<String>of();
			}
		}).thenAccept(MAX_LEVEL_ENCHANTMENTS::addAll);
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

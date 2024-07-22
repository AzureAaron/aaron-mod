package net.azureaaron.mod.utils;

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
import net.azureaaron.mod.commands.MagicalPowerCommand;
import net.azureaaron.mod.commands.NetworthCommand;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Http.ApiResponse;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

public class Skyblock {
	private static final Logger LOGGER = LogUtils.getLogger();
	//TODO refactor the codecs into their own classes
	private static final Codec<Map<String, ItemStack>> RARE_LOOT_CODEC = Codec.unboundedMap(Codec.STRING, ItemStack.CODEC);
	private static final Map<String, ItemStack> RARE_LOOT_ITEMS = new HashMap<>();
	
	private static final Codec<List<String>> MAX_ENCHANTMENTS_CODEC = Codec.list(Codec.STRING);
	private static final List<String> MAX_LEVEL_ENCHANTMENTS = new ArrayList<>();
	
	private static final Map<String, MagicalPowerCommand.MagicalPowerData> MAGICAL_POWERS = new HashMap<>();
	private static final Map<String, MagicalPowerCommand.Accessory> ACCESSORIES = new HashMap<>();
	
	private static boolean loaded;
	private static boolean enchantsLoaded;
		
	public static void init() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> CompletableFuture.allOf(loadRareLootItems(client), loadMaxEnchants(false), loadMagicalPowers(), loadAccessories())
				.whenComplete((_result, _throwable) -> loaded = true));
	}
	
	private static CompletableFuture<Void> loadRareLootItems(MinecraftClient client) {
		return CompletableFuture.supplyAsync(() -> {
			try (BufferedReader reader = client.getResourceManager().openAsReader(Identifier.of(Main.NAMESPACE, "skyblock/rare_loot_items.json"))) {
				RegistryOps<JsonElement> ops = ItemUtils.getRegistryLookup().getOps(JsonOps.INSTANCE);

				return RARE_LOOT_CODEC.parse(ops, JsonParser.parseReader(reader)).getOrThrow();
			} catch (Exception e) {
				LOGGER.error("[Aaron's Mod] Failed to load rare loot items file!", e);
				
				return Map.<String, ItemStack>of();
			}
		}).thenAccept(RARE_LOOT_ITEMS::putAll);
	}
	
	//Maybe load the enchants from file as backup?
	public static CompletableFuture<Void> loadMaxEnchants(boolean loadAnyways) {
		return CompletableFuture.supplyAsync(() -> {
			if ((AaronModConfigManager.get().rainbowifyMaxSkyblockEnchantments || AaronModConfigManager.get().enableSkyblockCommands || loadAnyways) && !enchantsLoaded) {
				try {
					ApiResponse response = Http.sendApiRequest("skyblock/maxenchantments");
					return MAX_ENCHANTMENTS_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(response.content())).getOrThrow();
				} catch (Exception e) {
					LOGGER.error("[Aaron's Mod] Failed to load max enchantments file!", e);
					
					return List.<String>of();
				}
			} else {
				return List.<String>of();
			}
		}).thenAccept(MAX_LEVEL_ENCHANTMENTS::addAll)
				.thenRun(() -> Functions.runIf(() -> enchantsLoaded = true, () -> !MAX_LEVEL_ENCHANTMENTS.isEmpty()));
	}
	
	private static CompletableFuture<Void> loadMagicalPowers() {
		return CompletableFuture.supplyAsync(() -> {
			if (AaronModConfigManager.get().enableSkyblockCommands) {
				try {
					ApiResponse response = Http.sendApiRequest("skyblock/magicalpowers");
					return MagicalPowerCommand.MagicalPowerData.MAP_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(response.content())).getOrThrow();
				} catch (Exception e) {
					LOGGER.error("[Aaron's Mod] Failed to load magical powers file!", e);
					
					return Map.<String, MagicalPowerCommand.MagicalPowerData>of();
				}
			} else {
				return Map.<String, MagicalPowerCommand.MagicalPowerData>of();
			}
		}).thenAccept(MAGICAL_POWERS::putAll);
	}
	
	private static CompletableFuture<Void> loadAccessories() {
		return CompletableFuture.supplyAsync(() -> {
			if (AaronModConfigManager.get().enableSkyblockCommands) {
				try {
					ApiResponse response = Http.sendApiRequest("skyblock/accessories");
					return MagicalPowerCommand.Accessory.MAP_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(response.content())).getOrThrow();
				} catch (Exception e) {
					LOGGER.error("[Aaron's Mod] Failed to load accessories!", e);
					
					return Map.<String, MagicalPowerCommand.Accessory>of();
				}
			} else {
				return Map.<String, MagicalPowerCommand.Accessory>of();
			}
		}).thenAccept(ACCESSORIES::putAll);
	}
	
	public static Map<String, ItemStack> getRareLootItems() {
		return loaded ? RARE_LOOT_ITEMS : Map.of();
	}
	
	public static List<String> getMaxEnchants() {
		return enchantsLoaded ? MAX_LEVEL_ENCHANTMENTS : List.of();
	}
	
	public static Map<String, MagicalPowerCommand.MagicalPowerData> getMagicalPowers() {
		return loaded ? MAGICAL_POWERS : Map.of();
	}
	
	public static Map<String, MagicalPowerCommand.Accessory> getAccessories() {
		return loaded ? ACCESSORIES : Map.of();
	}
	
	public static JsonObject getSelectedProfile2(String profiles) throws IllegalStateException {
		if (profiles == null) return null;
		
		JsonObject skyblockData = JsonParser.parseString(profiles).getAsJsonObject();
		
		if (skyblockData.get("profiles").isJsonNull()) throw new IllegalStateException(Messages.NO_SKYBLOCK_PROFILES_ERROR.get().getString()); //If the player's profile hasn't been migrated or they got wiped
		
		JsonArray profilesArray = skyblockData.getAsJsonArray("profiles");
				
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
		return switch ((Integer) score) {
			case Integer i when score == 0 -> "None";
			case Integer i when score >= 1000 -> "S";
			case Integer i when score >= 800 -> "A";
			case Integer i when score >= 600 -> "B";
			case Integer i when score >= 400 -> "C";
			case Integer i when score >= 200 -> "D";
			case Integer i when score < 200 -> "F";
			
			default -> "UNKNOWN";
		};
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

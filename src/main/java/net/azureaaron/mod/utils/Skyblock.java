package net.azureaaron.mod.utils;

import static net.azureaaron.mod.codecs.LootCodec.RARE_LOOT_CODEC;
import static net.azureaaron.mod.codecs.LootCodec.RARE_LOOT_ITEMS;

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
import com.mojang.serialization.JsonOps;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.commands.skyblock.MagicalPowerCommand;
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

	private static final Map<String, MagicalPowerCommand.MagicalPowerData> MAGICAL_POWERS = new HashMap<>();
	private static final Map<String, MagicalPowerCommand.Accessory> ACCESSORIES = new HashMap<>();
	
	private static boolean loaded;
		
	public static void init() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> CompletableFuture.allOf(loadRareLootItems(client), loadMagicalPowers(), loadAccessories())
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
	
	private static CompletableFuture<Void> loadMagicalPowers() {
		return CompletableFuture.supplyAsync(() -> {
			if (AaronModConfigManager.get().enableSkyblockCommands) {
				try {
					ApiResponse response = Http.sendAaronRequest("skyblock/magicalpowers");
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
					ApiResponse response = Http.sendAaronRequest("skyblock/accessories");
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
			if (iteratedProfile.get("selected").getAsBoolean()) return iteratedProfile;
		}
		
		throw new IllegalStateException(Messages.PROFILES_NOT_MIGRATED_ERROR.get().getString()); //After the migration players can apparently have no selected profile
	}
	
	public static boolean isInventoryApiEnabled(JsonObject inventoryData) {
		return inventoryData != null && inventoryData.has("inv_contents");
	}
	
	public static boolean isSkillsApiEnabled(JsonObject profile) {
		return profile.getAsJsonObject("player_data").has("experience");
	}
	
	public static String getDojoGrade(int score) {
		return switch ((Integer) score) {
			case Integer ignored5 when score == 0 -> "None";
			case Integer ignored4 when score >= 1000 -> "S";
			case Integer ignored3 when score >= 800 -> "A";
			case Integer ignored2 when score >= 600 -> "B";
			case Integer ignored1 when score >= 400 -> "C";
			case Integer ignored when score >= 200 -> "D";
			default -> "F";
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

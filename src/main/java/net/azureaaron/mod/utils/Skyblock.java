package net.azureaaron.mod.utils;

import static net.azureaaron.mod.codecs.LootCodec.RARE_LOOT_CODEC;
import static net.azureaaron.mod.codecs.LootCodec.RARE_LOOT_ITEMS;

import java.io.BufferedReader;
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
import net.azureaaron.mod.annotations.Init;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;

public class Skyblock {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static boolean loaded;

	@Init
	public static void init() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> CompletableFuture.allOf(loadRareLootItems(client))
				.whenComplete((_result, _throwable) -> loaded = true));
	}

	private static CompletableFuture<Void> loadRareLootItems(Minecraft client) {
		return CompletableFuture.supplyAsync(() -> {
			try (BufferedReader reader = client.getResourceManager().openAsReader(Main.id("skyblock/rare_loot_items.json"))) {
				RegistryOps<JsonElement> ops = ItemUtils.getRegistryLookup().createSerializationContext(JsonOps.INSTANCE);

				return RARE_LOOT_CODEC.parse(ops, JsonParser.parseReader(reader)).getOrThrow();
			} catch (Exception e) {
				LOGGER.error("[Aaron's Mod] Failed to load rare loot items file!", e);

				return Map.<String, ItemStack>of();
			}
		}).thenAccept(RARE_LOOT_ITEMS::putAll);
	}

	public static Map<String, ItemStack> getRareLootItems() {
		return loaded ? RARE_LOOT_ITEMS : Map.of();
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

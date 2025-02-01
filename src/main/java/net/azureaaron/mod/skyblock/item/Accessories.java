package net.azureaaron.mod.skyblock.item;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;

import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Http;

public class Accessories {
	private static final Logger LOGGER = LogUtils.getLogger();
	@Unmodifiable
	private static Map<String, Accessory> accessories = Map.of();
	@Unmodifiable
	private static Map<String, MagicalPower> magicalPowers = Map.of();

	public static void init() {
		loadAccessories();
		loadMagicalPowers();
	}

	private static void loadAccessories() {
		CompletableFuture.runAsync(() -> {
			if (AaronModConfigManager.get().enableSkyblockCommands) {
				try {
					String response = Http.sendGetRequest("https://api.azureaaron.net/skyblock/accessories");
					Map<String, Accessory> parsedAccessories = Accessory.MAP_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(response)).getOrThrow();

					accessories = parsedAccessories;
				} catch (Exception e) {
					LOGGER.error("[Aaron's Mod Accessories] Failed to download accessories!", e);
				}
			}
		});
	}

	private static void loadMagicalPowers() {
		CompletableFuture.runAsync(() -> {
			if (AaronModConfigManager.get().enableSkyblockCommands) {
				try {
					String response = Http.sendGetRequest("https://api.azureaaron.net/skyblock/magicalpowers");
					Map<String, MagicalPower> parsedMagicalPowers = MagicalPower.MAP_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(response)).getOrThrow();

					magicalPowers = parsedMagicalPowers;
				} catch (Exception e) {
					LOGGER.error("[Aaron's Mod Accessories] Failed to download magical powers!", e);
				}
			}
		});
	}

	public static Map<String, Accessory> getAccessories() {
		return accessories;
	}

	public static Map<String, MagicalPower> getMagicalPowers() {
		return magicalPowers;
	}
}

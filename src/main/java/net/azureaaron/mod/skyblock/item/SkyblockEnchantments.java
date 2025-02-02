package net.azureaaron.mod.skyblock.item;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;

import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Http;

public class SkyblockEnchantments {
	private static final Logger LOGGER = LogUtils.getLogger();
	@Unmodifiable
	private static Map<String, SkyblockEnchantment> enchantments = Map.of();

	@Init
	public static void init() {
		loadEnchantments(false);
	}

	public static void loadEnchantments(boolean loadAnyways) {
		CompletableFuture.runAsync(() -> {
			if (enchantments.isEmpty() && (AaronModConfigManager.get().rainbowifyMaxSkyblockEnchantments || loadAnyways)) {
				try {
					String response = Http.sendGetRequest("https://api.azureaaron.net/skyblock/enchantments");
					Map<String, SkyblockEnchantment> parsedEnchantments = SkyblockEnchantment.MAP_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(response)).getOrThrow();

					enchantments = parsedEnchantments;
				} catch (Exception e) {
					LOGGER.error("[Aaron's Mod Enchantments] Failed to download skyblock enchantments!", e);
				}
			}
		});
	}

	public static Map<String, SkyblockEnchantment> getEnchantments() {
		return enchantments;
	}
}

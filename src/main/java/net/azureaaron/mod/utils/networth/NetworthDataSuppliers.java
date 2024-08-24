package net.azureaaron.mod.utils.networth;

import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.azureaaron.mod.utils.CodecUtils;
import net.azureaaron.mod.utils.Http;
import net.azureaaron.mod.utils.JsonHelper;
import net.azureaaron.networth.data.SkyblockItemData;

public class NetworthDataSuppliers {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Codec<Object2DoubleMap<String>> CODEC = CodecUtils.object2DoubleMap(Codec.STRING);
	private static final long FIVE_MINUTES = 300_000;

	private static Object2ObjectMap<String, SkyblockItemData> itemData = Object2ObjectMaps.emptyMap();
	private static Object2DoubleMap<String> lbinPrices = Object2DoubleMaps.emptyMap();
	private static Object2DoubleMap<String> bazaarPrices = Object2DoubleMaps.emptyMap();
	private static volatile long pricesLastUpdated;

	static double getPrice(String apiId) {
		tick();

		if (bazaarPrices.containsKey(apiId)) {
			return bazaarPrices.getDouble(apiId);
		}

		return lbinPrices.getDouble(apiId);
	}

	static Object2ObjectMap<String, SkyblockItemData> getSkyblockItemData() {
		return itemData;
	}

	public static void updateSkyblockItemData(JsonArray items) {
		try {
			itemData = SkyblockItemData.MAP_CODEC.parse(JsonOps.INSTANCE, items).getOrThrow();
		} catch (Exception e) {
			LOGGER.error("[Aaron's Mod Networth Data Supplier] Failed to parse items data!", e);
		}
	}

	private static void tick() {
		//Refresh prices every 5 minutes
		if (System.currentTimeMillis() > pricesLastUpdated + FIVE_MINUTES) {
			try {
				String lbinPriceData = Http.sendGetRequest("https://hysky.de/api/auctions/lowestbins");
				Object2DoubleMap<String> lbins = CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(lbinPriceData)).getOrThrow();

				JsonObject bazaarPriceData = JsonParser.parseString(Http.sendGetRequest("https://hysky.de/api/bazaar")).getAsJsonObject();
				Object2DoubleMap<String> bazaar = bazaarPriceData.asMap().entrySet().stream()
						.map(e -> Pair.of(e.getKey(), e.getValue().getAsJsonObject()))
						.collect(Collectors.toMap(Pair::left, p -> JsonHelper.getDouble(p.right(), "buyPrice").orElse(0d), (a, b) -> a, Object2DoubleOpenHashMap::new));

				lbinPrices = lbins;
				bazaarPrices = bazaar;
				pricesLastUpdated = System.currentTimeMillis();
			} catch (Exception e) {
				LOGGER.error("[Aaron's Mod Networth Data Supplier] Failed to update prices!", e);
			}
		}
	}
}

package net.azureaaron.mod.config.datafixer;

import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.JsonHelper;
import net.fabricmc.loader.api.FabricLoader;

/**
 * DFU Data Fixers to handle conversions between the various config formats used by the mod over time.
 */
public class ConfigDataFixer {
	private static final Logger LOGGER = LogUtils.getLogger();
	/**
	 * {@link DSL.TypeReference} for the mod's config.
	 */
	protected static final DSL.TypeReference CONFIG_TYPE = () -> "aaron-mod:config";

	public static void apply() {
		try {
			if (Files.exists(AaronModConfigManager.PATH)) {
				JsonObject oldConfig = JsonParser.parseString(Files.readString(AaronModConfigManager.PATH)).getAsJsonObject();
				int version = JsonHelper.getInt(oldConfig, "version").orElse(1);

				//The version was updated aka the file needs data fixing
				if (version != AaronModConfigManager.VERSION) {
					long start = System.currentTimeMillis();
					DataFixer fixer = build();

					//Apply fixers
					Dynamic<JsonElement> fixed = fixer.update(CONFIG_TYPE, new Dynamic<>(JsonOps.INSTANCE, oldConfig), version, AaronModConfigManager.VERSION);
					JsonObject newConfig = fixed.getValue().getAsJsonObject();

					LOGGER.info("[Aaron's Mod Config Data Fixer] Successfully applied data fixers in {} ms!", System.currentTimeMillis() - start);
					if (!saveConfig(AaronModConfigManager.PATH, newConfig)) {
						Path fallback = FabricLoader.getInstance().getConfigDir().resolve("aaron-mod.json.old");

						LOGGER.error(LogUtils.FATAL_MARKER, "[Aaron's Mod Config Data Fixer] Failed to write new config file! Saving old one at: {}", fallback);
						saveConfig(fallback, oldConfig);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("[Aaron's Mod Config Data Fixer] Failed to apply config data fixers!", e);
		}
	}

	/**
	 * Attempts to write a config file to the {@code path}; returns a boolean depending on whether the write was successful or not.
	 */
	private static boolean saveConfig(Path path, JsonObject config) {
		try {
			Files.writeString(path, Main.GSON.toJson(config));

			return true;
		} catch (Exception e) {
			LOGGER.error("[Aaron's Mod Config Data Fixer] Failed to write config to {}.", e);
		}

		return false;
	}

	/**
	 * Builds the data fixers.
	 */
	@VisibleForTesting
	protected static DataFixer build() {
		DataFixerBuilder builder = new DataFixerBuilder(AaronModConfigManager.VERSION);

		builder.addSchema(1, ConfigSchema::new);

		Schema schema2 = builder.addSchema(2, Schema::new);
		builder.addFixer(new ConfigFixV1(schema2, true));

		Schema schema3 = builder.addSchema(3, Schema::new);
		builder.addFixer(new ConfigFixV2(schema3, true));

		return builder.build().fixer();
	}
}

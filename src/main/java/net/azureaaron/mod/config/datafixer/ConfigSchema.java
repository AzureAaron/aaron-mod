package net.azureaaron.mod.config.datafixer;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

/**
 * Basic {@link Schema} to represent the structure of the config.
 */
public class ConfigSchema extends Schema {
	public ConfigSchema(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		schema.registerType(true, ConfigDataFixer.CONFIG_TYPE, DSL::remainder);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
		return Map.of();
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {
		return Map.of();
	}
}

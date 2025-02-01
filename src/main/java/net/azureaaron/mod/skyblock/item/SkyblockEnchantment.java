package net.azureaaron.mod.skyblock.item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.azureaaron.mod.utils.CodecUtils;

public record SkyblockEnchantment(String id, String name, int maxLevel, OptionalInt goodLevel, Optional<List<String>> conflicts) {
	private static final Codec<SkyblockEnchantment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("id").forGetter(SkyblockEnchantment::id),
			Codec.STRING.fieldOf("name").forGetter(SkyblockEnchantment::name),
			Codec.INT.fieldOf("maxLevel").forGetter(SkyblockEnchantment::maxLevel),
			CodecUtils.optionalInt(Codec.INT.optionalFieldOf("goodLevel")).forGetter(SkyblockEnchantment::goodLevel),
			Codec.STRING.listOf().optionalFieldOf("conflicts").forGetter(SkyblockEnchantment::conflicts))
			.apply(instance, SkyblockEnchantment::new));
	public static final Codec<Map<String, SkyblockEnchantment>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC);

	/**
	 * Checks if the enchant is at a "max" level, which is when {@code level} greater than or equal to the enchant's {@link #maxLevel}.
	 */
	public boolean isAtMaxLevel(int level) {
		return level >= maxLevel;
	}

	/**
	 * Checks if the enchant is at a "good" level which is when {@code level} is greater than or equal to the {@link #goodLevel}
	 * while {@link #isMax(int)} is false.
	 */
	public boolean isAtGoodLevel(int level) {
		return goodLevel.isPresent() && level >= goodLevel.getAsInt() && !isAtMaxLevel(level);
	}

	public boolean isAtGoodOrMaxLevel(int level) {
		return isAtMaxLevel(level) || isAtGoodLevel(level);
	}

	/**
	 * Checks if {@code other} is marked as a conflicting enchantment.
	 */
	public boolean conflictsWith(String other) {
		return conflicts.isPresent() && conflicts.get().contains(other);
	}

	public enum Type {
		NORMAL,
		ULTIMATE,
		STACKING
	}
}

package net.azureaaron.mod.skyblock.item;

import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Accessory(String id, Optional<String> family, int tier) {
	private static final Codec<Accessory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("id").forGetter(Accessory::id),
			Codec.STRING.optionalFieldOf("family").forGetter(Accessory::family),
			Codec.INT.optionalFieldOf("tier", 0).forGetter(Accessory::tier))
			.apply(instance, Accessory::new));
	public static final Codec<Map<String, Accessory>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC);

	public static Accessory fromId(String id) {
		return new Accessory(id, Optional.empty(), 0);
	}

	public boolean hasSameFamily(Accessory other) {
		return other.family().equals(this.family);
	}
}

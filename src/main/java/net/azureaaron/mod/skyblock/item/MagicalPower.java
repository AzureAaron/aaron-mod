package net.azureaaron.mod.skyblock.item;

import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.Codecs.StrictUnboundedMapCodec;

public record MagicalPower(Object2FloatOpenHashMap<String> stats, Object2FloatOpenHashMap<String> bonus) {
	private static final Codec<MagicalPower> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codecs.strictUnboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("stats").forGetter(MagicalPower::stats),
			Codecs.strictUnboundedMap(Codec.STRING, Codec.FLOAT).optionalFieldOf("bonus").forGetter(MagicalPower::bonusOptional))
			.apply(instance, MagicalPower::new));
	public static final StrictUnboundedMapCodec<String, MagicalPower> MAP_CODEC = Codecs.strictUnboundedMap(Codec.STRING, MagicalPower.CODEC);

	private MagicalPower(Map<String, Float> stats, Optional<Map<String, Float>> bonus) {
		this(new Object2FloatOpenHashMap<>(stats), new Object2FloatOpenHashMap<String>(bonus.orElse(Map.of())));
	}

	public Optional<Map<String, Float>> bonusOptional() {
		return Optional.of(bonus);
	}
}

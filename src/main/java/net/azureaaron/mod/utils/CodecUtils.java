package net.azureaaron.mod.utils;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

public class CodecUtils {

	public static <K> Codec<Object2DoubleMap<K>> object2DoubleMap(Codec<K> keyCodec) {
		return Codec.unboundedMap(keyCodec, Codec.DOUBLE).xmap(Object2DoubleOpenHashMap::new, Function.identity());
	}

	public static MapCodec<OptionalInt> optionalInt(MapCodec<Optional<Integer>> codec) {
		return codec.xmap(opt -> opt.map(OptionalInt::of).orElseGet(OptionalInt::empty), optInt -> optInt.isPresent() ? Optional.of(optInt.getAsInt()) : Optional.empty());
	}
}

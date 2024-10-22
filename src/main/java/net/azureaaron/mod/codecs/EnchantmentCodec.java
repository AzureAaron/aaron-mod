package net.azureaaron.mod.codecs;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

public class EnchantmentCodec {

	public static final Codec<List<String>> MAX_ENCHANTMENTS_CODEC = Codec.list(Codec.STRING);
	public static final List<String> MAX_LEVEL_ENCHANTMENTS = new ArrayList<>();

}

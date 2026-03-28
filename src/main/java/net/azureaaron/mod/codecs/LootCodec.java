package net.azureaaron.mod.codecs;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.item.ItemStackTemplate;

import com.mojang.serialization.Codec;

public class LootCodec {
	public static final Codec<Map<String, ItemStackTemplate>> RARE_LOOT_CODEC = Codec.unboundedMap(Codec.STRING, ItemStackTemplate.CODEC);
	public static final Map<String, ItemStackTemplate> RARE_LOOT_ITEMS = new HashMap<>();
}

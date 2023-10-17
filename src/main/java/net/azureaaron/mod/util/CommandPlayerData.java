package net.azureaaron.mod.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Record which holds a player's name and uuid, similar to a {@link com.mojang.authlib.GameProfile GameProfile}.
 * This is used for commands that take in a player argument.
 * 
 * @author Aaron
 */
public record CommandPlayerData(String name, String uuid) {
	public static final Codec<CommandPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(CommandPlayerData::name),
			Codec.STRING.fieldOf("uuid").forGetter(CommandPlayerData::uuid)
			).apply(instance, CommandPlayerData::new));
}

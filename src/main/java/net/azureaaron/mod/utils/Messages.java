package net.azureaaron.mod.utils;

import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Message constant class containing texts used throughout the mod.
 *
 * @author Aaron
 */
//TODO rename to CommandMessages and move to another package?
public interface Messages {

	/*
	 * Global/Multipurpose messages
	 */

	Supplier<MutableComponent> UNKNOWN_ERROR = () -> Constants.PREFIX.get().append(Component.literal("An unknown error occured!")
			.withStyle(ChatFormatting.RED));

	Supplier<MutableComponent> NAME_TO_UUID_ERROR = () -> Constants.PREFIX.get().append(Component.literal("There was an error while converting a username to a uuid! Make sure the player's name was spelled correctly!")
			.withStyle(ChatFormatting.RED));

	Supplier<MutableComponent> UUID_TO_NAME_ERROR = () -> Constants.PREFIX.get().append(Component.literal("There was an error while converting a uuid to a username! Make sure the player's uuid is valid!")
			.withStyle(ChatFormatting.RED));

	Supplier<MutableComponent> SKYBLOCK_PROFILES_FETCH_ERROR = () -> Constants.PREFIX.get().append(Component.literal("There was an error fetching skyblock profiles!")
			.withStyle(ChatFormatting.RED));

	Supplier<MutableComponent> NO_SKYBLOCK_PROFILES_ERROR = () -> Constants.PREFIX.get().append(Component.literal("This player doesn't have any skyblock profiles!")
			.withStyle(ChatFormatting.RED));

	Supplier<MutableComponent> JSON_PARSING_ERROR = () -> Constants.PREFIX.get().append(Component.literal("There was an error while trying to parse JSON!")
			.withStyle(ChatFormatting.RED));

	Supplier<MutableComponent> PROFILES_NOT_MIGRATED_ERROR = () -> Constants.PREFIX.get().append(Component.literal("This player hasn't migrated their skyblock profiles!")
			.withStyle(ChatFormatting.RED));

	/*
	 * Class-specific messages that don't fit well in their dedicated class
	 */

	Supplier<MutableComponent> INVENTORY_API_DISABLED_ERROR = () -> Constants.PREFIX.get().append(Component.literal("This player has their inventory api disabled!")
			.withStyle(ChatFormatting.RED));
}

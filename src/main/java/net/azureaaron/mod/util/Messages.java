package net.azureaaron.mod.util;

import java.util.function.Supplier;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Message constant class containing texts used throughout the mod.
 * 
 * @author Aaron
 */
public interface Messages {
	
	/*
	 * Global/Multipurpose messages
	 */
	
	Supplier<MutableText> UNKNOWN_ERROR = () -> Constants.PREFIX.get().append(Text.literal("An unknown error occured!")
			.formatted(Formatting.RED));
	
	Supplier<MutableText> NAME_TO_UUID_ERROR = () -> Constants.PREFIX.get().append(Text.literal("There was an error while converting a username to a uuid! Make sure the player's name was spelled correctly!")
			.formatted(Formatting.RED));
	
	Supplier<MutableText> UUID_TO_NAME_ERROR = () -> Constants.PREFIX.get().append(Text.literal("There was an error while converting a uuid to a username! Make sure the player's uuid is valid!")
			.formatted(Formatting.RED));
	
	Supplier<MutableText> SKYBLOCK_PROFILES_FETCH_ERROR = () -> Constants.PREFIX.get().append(Text.literal("There was an error fetching skyblock profiles!")
			.formatted(Formatting.RED));
	
	Supplier<MutableText> NO_SKYBLOCK_PROFILES_ERROR = () -> Constants.PREFIX.get().append(Text.literal("This player doesn't have any skyblock profiles!")
			.formatted(Formatting.RED));
	
	Supplier<MutableText> JSON_PARSING_ERROR = () -> Constants.PREFIX.get().append(Text.literal("There was an error while trying to parse JSON!")
			.formatted(Formatting.RED));
	
	Supplier<MutableText> PROFILES_NOT_MIGRATED_ERROR = () -> Constants.PREFIX.get().append(Text.literal("This player hasn't migrated their skyblock profiles!")
			.formatted(Formatting.RED));
	
	/*
	 * Class-specific messages that don't fit well in their dedicated class
	 */
	
	Supplier<MutableText> HYPIXEL_PROFILE_FETCH_ERROR = () -> Constants.PREFIX.get().append(Text.literal("There was an error fetching your Hypixel profile!")
			.formatted(Formatting.RED));
	
	Supplier<MutableText> INVENTORY_API_DISABLED_ERROR = () -> Constants.PREFIX.get().append(Text.literal("This player has their inventory api disabled!")
			.formatted(Formatting.RED));
}

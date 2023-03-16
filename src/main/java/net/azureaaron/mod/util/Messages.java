package net.azureaaron.mod.util;

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
	
	Text UNKNOWN_ERROR = Text.literal("An unknown error occured!")
			.styled(style -> style.withColor(Formatting.RED));
	
	Text NAME_TO_UUID_ERROR = Text.literal("There was an error while converting a username to a uuid!\n Make sure the player's name was spelled correctly!")
			.styled(style -> style.withColor(Formatting.RED));
	
	Text SKYBLOCK_PROFILES_FETCH_ERROR = Text.literal("There was an error fetching skyblock profiles!")
			.styled(style -> style.withColor(Formatting.RED));
	
	Text NO_SKYBLOCK_PROFILES_ERROR = Text.literal("This player doesn't have any skyblock profiles!")
			.styled(style -> style.withColor(Formatting.RED));
	
	Text JSON_PARSING_ERROR = Text.literal("There was an error while trying to parse JSON!")
			.styled(style -> style.withColor(Formatting.RED));
	
	Text NO_API_KEY_ERROR = Text.literal("You haven't set your Hypixel Api Key!")
			.styled(style -> style.withColor(Formatting.RED));
	
	/*
	 * Class-specific messages that don't fit well in their dedicated class
	 */
	
	Text HYPIXEL_PROFILE_FETCH_ERROR = Text.literal("There was an error fetching your Hypixel profile!")
			.styled(style -> style.withColor(Formatting.RED));
	
	Text INVENTORY_API_DISABLED_ERROR = Text.literal("This player has their inventory api disabled!")
			.styled(style -> style.withColor(Formatting.RED));
}

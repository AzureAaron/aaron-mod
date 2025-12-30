package net.azureaaron.mod.commands;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;

import net.azureaaron.mod.features.TextReplacer;
import net.azureaaron.mod.utils.ApiUtils;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.Http;
import net.azureaaron.mod.utils.Messages;
import net.azureaaron.mod.utils.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * Provides core functionality for the mod's commands.
 *
 * @author Aaron
 */
public class CommandSystem {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Supplier<Font> TEXT_RENDERER = () -> Minecraft.getInstance().font;

	/**
	 * Ensures that "dummy" players aren't included in command suggestions
	 */
	public static String[] getPlayerSuggestions(FabricClientCommandSource source) {
		return source.getOnlinePlayerNames().stream().filter(playerName -> playerName.matches("[A-Za-z0-9_]+")).toArray(String[]::new);
	}

	public static String getEndSpaces(Component text) {
		Font textRenderer = TEXT_RENDERER.get();

		int spaceWidth = textRenderer.width(" ");
		int textWidth = textRenderer.width(TextReplacer.visuallyReplaceText(text.getVisualOrderText()));
		int spacesNeeded = (int) Math.ceil((double) textWidth / (double) spaceWidth);

		String spaces = "";

		for (int i = 0; i < spacesNeeded; i++) {
			spaces += " ";
		}

		return spaces;
	}

	/**
	 * Handles the command for the current player
	 */
	public static int handleSelf4Skyblock(SkyblockCommand command, FabricClientCommandSource source) {
		User session = source.getClient().getUser();

		return handleSkyblockCommand(command, source, new CommandPlayerData(session.getName(), session.getProfileId().toString().replaceAll("-", "")));
	}

	/**
	 * Handles the command for another player
	 */
	public static int handlePlayer4Skyblock(SkyblockCommand command, FabricClientCommandSource source, String player) {
		CompletableFuture.supplyAsync(() -> {
			try {
				return lookupPlayer(player);
			} catch (Throwable t) {
				if (!Functions.isUuid(player)) {
					source.sendError(Messages.NAME_TO_UUID_ERROR.get());
				} else {
					source.sendError(Messages.UUID_TO_NAME_ERROR.get());
				}

				LOGGER.error("[Aaron's Mod] Encountered an exception while resolving a player's uuid/username!", t);

				return null;
			}
		})
		.thenAccept(playerData -> {
			if (playerData != null) handleSkyblockCommand(command, source, playerData);
		});

		return Command.SINGLE_SUCCESS;
	}

	private static int handleSkyblockCommand(SkyblockCommand command, FabricClientCommandSource source, CommandPlayerData playerData) {
		CompletableFuture.supplyAsync(() -> {
			try {
				return Http.sendHypixelRequest("skyblock/profiles", "?uuid=" + playerData.id());
			} catch (Throwable t) {
				source.sendError(Messages.SKYBLOCK_PROFILES_FETCH_ERROR.get());
				LOGGER.error("[Aaron's Mod] Encountered an exception while fetching a player's skyblock profiles!", t);

				return null;
			}
		})
		.thenApply(body -> {
			try {
				return Skyblock.getSelectedProfile2(body);
			} catch (Throwable t) {
				if (t instanceof IllegalStateException) source.sendError(Messages.NO_SKYBLOCK_PROFILES_ERROR.get()); else source.sendError(Messages.JSON_PARSING_ERROR.get());
				LOGGER.error("[Aaron's Mod] Encountered an exception while determining a player's selected skyblock profile!", t);

				return null;
			}
		})
		.thenAccept(profileData -> {
			if (profileData != null) {
				try {
					command.print(source, profileData, playerData.name(), playerData.id());
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR.get());
					LOGGER.error("[Aaron's Mod] Encountered an exception while dispatching a skyblock command! Command: {}", command.getClass().getName(), t);
				}
			}
		});

		return Command.SINGLE_SUCCESS;
	}

	public static int handleSelf4Vanilla(VanillaCommand command, FabricClientCommandSource source) {
		User session = source.getClient().getUser();

		try {
			command.print(source, session.getName(), session.getProfileId().toString().replaceAll("-", ""));
		} catch (Throwable t) {
			source.sendError(Messages.UNKNOWN_ERROR.get());
			LOGGER.error("[Aaron's Mod] Encountered an exception while dispatching a vanilla command! Command: {}", command.getClass().getName(), t);
		}

		return Command.SINGLE_SUCCESS;
	}

	public static int handlePlayer4Vanilla(VanillaCommand command, FabricClientCommandSource source, String player) {
		CompletableFuture.supplyAsync(() -> {
			try {
				return lookupPlayer(player);
			} catch (Throwable t) {
				if (!Functions.isUuid(player)) {
					source.sendError(Messages.NAME_TO_UUID_ERROR.get());
				} else {
					source.sendError(Messages.UUID_TO_NAME_ERROR.get());
				}

				LOGGER.error("[Aaron's Mod] Encountered an exception while resolving a player's uuid/username!", t);

				return null;
			}
		})
		.thenAccept(playerData -> {
			if (playerData != null) {
				try {
					command.print(source, playerData.name(), playerData.id());
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR.get());
					LOGGER.error("[Aaron's Mod] Encountered an exception while dispatching a vanilla command! Command: {}", command.getClass().getName(), t);
				}
			}
		});

		return Command.SINGLE_SUCCESS;
	}

	private static CommandPlayerData lookupPlayer(String player) throws Exception {
		GameProfile profile = ApiUtils.getProfile(player);

		if (profile != null) {
			return new CommandPlayerData(profile.name(), UndashedUuid.toString(profile.id()));
		} else {
			throw new IllegalStateException("Failed to fetch the GameProfile for " + player);
		}
	}
}

package net.azureaaron.mod.commands;

import java.lang.StackWalker.Option;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;

import net.azureaaron.mod.features.TextReplacer;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.Http;
import net.azureaaron.mod.utils.Messages;
import net.azureaaron.mod.utils.Skyblock;
import net.azureaaron.mod.utils.Http.ApiResponse;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;

/**
 * Provides core functionality for the mod's commands.
 * 
 * @author Aaron
 */
public class CommandSystem {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Supplier<TextRenderer> TEXT_RENDERER = () -> MinecraftClient.getInstance().textRenderer;
	
	/**
	 * Ensures that "dummy" players aren't included in command suggestions
	 */
	public static String[] getPlayerSuggestions(FabricClientCommandSource source) {
		return source.getPlayerNames().stream().filter(playerName -> playerName.matches("[A-Za-z0-9_]+")).toArray(String[]::new);
	}
	
	public static String getEndSpaces(Text text) {
		TextRenderer textRenderer = TEXT_RENDERER.get();
		
		int spaceWidth = textRenderer.getWidth(" ");
		int textWidth = textRenderer.getWidth(TextReplacer.visuallyReplaceText(text.asOrderedText()));
		int spacesNeeded = (int) Math.ceil((double) textWidth / (double) spaceWidth);
		
		String spaces = "";
		
		for (int i = 0; i < spacesNeeded; i++) {
			spaces += " ";
		}
		
		return spaces;
	}
	
	/**
	 * Specialized to skyblock commands!
	 * @param printMethod The name of the command's print method
	 * 
	 * @return A {@link java.lang.invoke.MethodHandle MethodHandle} for the command's print method.
	 */
	public static MethodHandle obtainDispatchHandle4Skyblock(String printMethod) {
		Class<?> cmdClass = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).getCallerClass();
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType mt = MethodType.methodType(void.class, FabricClientCommandSource.class, JsonObject.class, String.class, String.class);
		MethodHandle handle = null;
		
		try {
			handle = lookup.findStatic(cmdClass, printMethod, mt);
		} catch (Throwable t) {
			LOGGER.error("[Aaron's Mod] Encountered an exception while obtaining a skyblock command dispatch handle!", t);
		}
		
		return handle;
	}
	
	/**
	 * Handles the command for the current player
	 */
	public static int handleSelf4Skyblock(FabricClientCommandSource source, MethodHandle dispatchHandle) {
		Session session = source.getClient().getSession();
		
		return handleSkyblockCommand(source, new CommandPlayerData(session.getUsername(), session.getUuidOrNull().toString().replaceAll("-", "")), dispatchHandle);
	}
	
	/**
	 * Handles the command for another player
	 */
	public static int handlePlayer4Skyblock(FabricClientCommandSource source, String player, MethodHandle dispatchHandle) {
		CompletableFuture.supplyAsync(() -> {
			try {
				boolean isName = !Functions.isUuid(player);
				
				return nameToUuid(player, isName, 0);
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
			if (playerData != null) handleSkyblockCommand(source, playerData, dispatchHandle);
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int handleSkyblockCommand(FabricClientCommandSource source, CommandPlayerData playerData, MethodHandle dispatchHandle) {
		CompletableFuture.supplyAsync(() -> {
			try {
				//TODO remove this legacy profiles v1 thing when the networth api updates
				return Http.sendAuthorizedHypixelRequest(dispatchHandle == NetworthCommand.DISPATCH_HANDLE ? "skyblock/profiles" : "v2/skyblock/profiles", "?uuid=" + playerData.id());
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
					dispatchHandle.invokeExact(source, profileData, playerData.name(), playerData.id());
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR.get());
					LOGGER.error("[Aaron's Mod] Encountered an exception while dispatching a skyblock command! Handle: {}", dispatchHandle.describeConstable(), t);
				}
			}
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	/**
	 * Specialized to vanilla commands!
	 * @param printMethod The name of the command's print method
	 * 
	 * @return A {@link java.lang.invoke.MethodHandle MethodHandle} for the command's print method.
	 */
	public static MethodHandle obtainDispatchHandle4Vanilla(String printMethod) {
		Class<?> cmdClass = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).getCallerClass();
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType mt = MethodType.methodType(void.class, FabricClientCommandSource.class, String.class, String.class);
		MethodHandle handle = null;
		
		try {
			handle = lookup.findStatic(cmdClass, printMethod, mt);
		} catch (Throwable t) {
			LOGGER.error("[Aaron's Mod] Encountered an exception while obtaining a vanilla command dispatch handle!", t);
		}
		
		return handle;
	}
	
	public static int handleSelf4Vanilla(FabricClientCommandSource source, MethodHandle dispatchHandle) {
		Session session = source.getClient().getSession();
		
		try {
			dispatchHandle.invokeExact(source, session.getUsername(), session.getUuidOrNull().toString().replaceAll("-", ""));
		} catch (Throwable t) {
			source.sendError(Messages.UNKNOWN_ERROR.get());
			LOGGER.error("[Aaron's Mod] Encountered an exception while dispatching a vanilla command! Handle: {}", dispatchHandle.describeConstable(), t);
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	public static int handlePlayer4Vanilla(FabricClientCommandSource source, String player, MethodHandle dispatchHandle) {
		CompletableFuture.supplyAsync(() -> {
			try {
				boolean isName = !Functions.isUuid(player);
				
				return nameToUuid(player, isName, 0);
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
					dispatchHandle.invokeExact(source, playerData.name(), playerData.id());
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR.get());
					LOGGER.error("[Aaron's Mod] Encountered an exception while dispatching a vanilla command! Handle: {}", dispatchHandle.describeConstable(), t);
				}
			}
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static CommandPlayerData nameToUuid(String player, boolean isName, int retries) throws Exception {
		ApiResponse response = isName ? Http.sendNameToUuidRequest(player) : Http.sendUuidToNameRequest(player);
		
		if (response.ok()) {
			return CommandPlayerData.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(response.content())).getOrThrow();
		} else if (response.ratelimited() && retries < 3) {
			Thread.sleep(800);
			
			return nameToUuid(player, isName, ++retries);
		} else {
			throw response.createException();
		}
	}
}

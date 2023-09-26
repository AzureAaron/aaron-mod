package net.azureaaron.mod.commands;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;

import net.azureaaron.mod.util.CommandPlayerData;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.Messages;
import net.azureaaron.mod.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.session.Session;

/**
 * Provides core functionality for the mod's commands.
 * 
 * @author Aaron
 */
public class CommandSystem {
	
	/**
	 * Ensures that "dummy" players aren't included in command suggestions
	 */
	public static String[] getPlayerSuggestions(FabricClientCommandSource source) {
		return source.getPlayerNames().stream().filter(playerName -> playerName.matches("[A-Za-z0-9_]+")).toArray(String[]::new);
	}
	
	/**
	 * Specialized to skyblock commands!
	 * 
	 * @param cmdClass The class of the command
	 * @param printMethod The name of the command's print method
	 * @return A {@link java.lang.invoke.MethodHandle MethodHandle} for the command's print method.
	 */
	public static MethodHandle obtainDispatchHandle4Skyblock(Class<?> cmdClass, String printMethod) {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType mt = MethodType.methodType(void.class, FabricClientCommandSource.class, JsonObject.class, String.class, String.class);
		MethodHandle handle = null;
		
		try {
			handle = lookup.findStatic(cmdClass, printMethod, mt);
		} catch (Throwable t) {
			t.printStackTrace();
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
				String response = Http.sendNameToUuidRequest(player);
				JsonObject json = JsonParser.parseString(response).getAsJsonObject();
				String name = json.get("name").getAsString();
				String uuid = json.get("id").getAsString();
				
				return new CommandPlayerData(name, uuid);
			} catch (Throwable t) {
				source.sendError(Messages.NAME_TO_UUID_ERROR);
				t.printStackTrace();
				
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
				return Http.sendAuthorizedHypixelRequest("skyblock/profiles", "?uuid=" + playerData.uuid());
			} catch (Throwable t) {
				source.sendError(Messages.SKYBLOCK_PROFILES_FETCH_ERROR);
				t.printStackTrace();
				
				return null;
			}
		})
		.thenApply(body -> {
			try {
				return Skyblock.getSelectedProfile2(body);
			} catch (Throwable t) {
				if (t instanceof IllegalStateException) source.sendError(Messages.PROFILES_NOT_MIGRATED_ERROR); else source.sendError(Messages.JSON_PARSING_ERROR);
				t.printStackTrace();
				
				return null;
			}
		})
		.thenAccept(profileData -> {
			if (profileData != null) {
				try {
					dispatchHandle.invokeExact(source, profileData, playerData.name(), playerData.uuid());
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR);
					t.printStackTrace();
				}
			}
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	/**
	 * Specialized to vanilla commands!
	 * 
	 * @param cmdClass The class of the command
	 * @param printMethod The name of the command's print method
	 * @return A {@link java.lang.invoke.MethodHandle MethodHandle} for the command's print method.
	 */
	public static MethodHandle obtainDispatchHandle4Vanilla(Class<?> cmdClass, String printMethod) {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType mt = MethodType.methodType(void.class, FabricClientCommandSource.class, String.class, String.class);
		MethodHandle handle = null;
		
		try {
			handle = lookup.findStatic(cmdClass, printMethod, mt);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		return handle;
	}
	
	public static int handleSelf4Vanilla(FabricClientCommandSource source, MethodHandle dispatchHandle) {
		Session session = source.getClient().getSession();
		
		try {
			dispatchHandle.invokeExact(source, session.getUsername(), session.getUuidOrNull().toString().replaceAll("-", ""));
		} catch (Throwable t) {
			source.sendError(Messages.UNKNOWN_ERROR);
			t.printStackTrace();
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	public static int handlePlayer4Vanilla(FabricClientCommandSource source, String player, MethodHandle dispatchHandle) {
		CompletableFuture.supplyAsync(() -> {
			try {
				String response = Http.sendNameToUuidRequest(player);
				JsonObject json = JsonParser.parseString(response).getAsJsonObject();
				String name = json.get("name").getAsString();
				String uuid = json.get("id").getAsString();
				
				return new CommandPlayerData(name, uuid);
			} catch (Throwable t) {
				source.sendError(Messages.NAME_TO_UUID_ERROR);
				t.printStackTrace();
				
				return null;
			}
		})
		.thenAccept(playerData -> {
			if (playerData != null) {
				try {
					dispatchHandle.invokeExact(source, playerData.name(), playerData.uuid());
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR);
					t.printStackTrace();
				}
			}
		});
		
		return Command.SINGLE_SUCCESS;
	}
}

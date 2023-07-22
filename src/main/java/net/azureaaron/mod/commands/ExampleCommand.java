package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.CommandPlayerData;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.Messages;
import net.azureaaron.mod.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.util.Session;
import net.minecraft.command.CommandSource;

public class ExampleCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("example")
				.executes(context -> handleSelf(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandPlayerData.getPlayerNames(context.getSource()), builder))
						.executes(context -> handlePlayer(context.getSource(), getString(context, "player")))));
	}
	
	private static int handleSelf(FabricClientCommandSource source) {
		Session session = source.getClient().getSession();
		
		return handleCommand(source, new CommandPlayerData(session.getUsername(), session.getUuid()));
	}
	
	private static int handlePlayer(FabricClientCommandSource source, String player) {
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
			if (playerData != null) handleCommand(source, playerData);
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int handleCommand(FabricClientCommandSource source, CommandPlayerData playerData) {
		if (StringUtils.isBlank(Config.key)) {
			source.sendError(Messages.NO_API_KEY_ERROR);
			return Command.SINGLE_SUCCESS;
		}
		
		CompletableFuture.supplyAsync(() -> {
			try {
				return Http.sendHypixelRequest("skyblock/profiles", "&uuid=" + playerData.uuid(), true);
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
					print(source, profileData, playerData.name(), playerData.uuid());
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR);
					t.printStackTrace();
				}
			}
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static void print(FabricClientCommandSource source, JsonObject profileData, String name, String uuid) {
		//Do stuff here
	}
}

package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
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
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.Messages;
import net.azureaaron.mod.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.util.Session;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class EssenceCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("essence")
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
					printEssence(source, profileData, playerData.name(), playerData.uuid());
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR);
					t.printStackTrace();
				}
			}
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static void printEssence(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		JsonObject profile = body.get("members").getAsJsonObject().get(uuid).getAsJsonObject();
		String endSpaces = "        " + name.replaceAll("[A-z0-9_]", "  ") + "        ";
		
		int witherEssence = profile.get("essence_wither") != null ? profile.get("essence_wither").getAsInt() : 0;
		int spiderEssence = profile.get("essence_spider") != null ? profile.get("essence_spider").getAsInt() : 0;
		int undeadEssence = profile.get("essence_undead") != null ? profile.get("essence_undead").getAsInt() : 0;
		int dragonEssence = profile.get("essence_dragon") != null ? profile.get("essence_dragon").getAsInt() : 0;
		int goldEssence = profile.get("essence_gold") != null ? profile.get("essence_gold").getAsInt() : 0;
		int diamondEssence = profile.get("essence_diamond") != null ? profile.get("essence_diamond").getAsInt() : 0;
		int iceEssence = profile.get("essence_ice") != null ? profile.get("essence_ice").getAsInt() : 0;
		int crimsonEssence = profile.get("essence_crimson") != null ? profile.get("essence_crimson").getAsInt() : 0;
		
		source.sendFeedback(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true)))));
		
		source.sendFeedback(Text.literal("Wither » " + Functions.NUMBER_FORMATTER_ND.format(witherEssence)).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Spider » " + Functions.NUMBER_FORMATTER_ND.format(spiderEssence)).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Undead » " + Functions.NUMBER_FORMATTER_ND.format(undeadEssence)).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Dragon » " + Functions.NUMBER_FORMATTER_ND.format(dragonEssence)).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Gold » " + Functions.NUMBER_FORMATTER_ND.format(goldEssence)).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Diamond » " + Functions.NUMBER_FORMATTER_ND.format(diamondEssence)).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Ice » " + Functions.NUMBER_FORMATTER_ND.format(iceEssence)).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Crimson » " + Functions.NUMBER_FORMATTER_ND.format(crimsonEssence)).styled(style -> style.withColor(colourProfile.infoColour)));
		
		source.sendFeedback(Text.literal(endSpaces).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
		
		return;
	}
}

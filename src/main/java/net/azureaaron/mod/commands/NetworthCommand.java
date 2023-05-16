package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.Messages;
import net.azureaaron.mod.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.util.Session;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NetworthCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("networth")
				.executes(context -> handleCommand(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "player")))));
		
		dispatcher.register(literal("nw")
				.executes(context -> handleCommand(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "player")))));
	}
	
	private static final Text NETWORTH_FETCH_ERROR = Text.literal("There was an error while fetching a player's networth!").styled(style -> style.withColor(Formatting.RED));

	private static int handleCommand(FabricClientCommandSource source) {
		if(StringUtils.isBlank(Config.key)) {
			source.sendFeedback(Messages.NO_API_KEY_ERROR);
			return Command.SINGLE_SUCCESS;
		}
		
		Session session = source.getClient().getSession();
						
		CompletableFuture.supplyAsync(() -> {
			try {
				return Http.sendHypixelRequest("skyblock/profiles", "&uuid=" + session.getUuid(), true, false);
			} catch (Exception e) {
				source.sendError(Messages.SKYBLOCK_PROFILES_FETCH_ERROR);
				e.printStackTrace();
			}
			return null;
		})
		.thenApply(body -> {
			try {
				return Skyblock.getSelectedProfile2(body);
			} catch (Exception e) {
				if(e instanceof IllegalStateException) source.sendError(Messages.PROFILES_NOT_MIGRATED_ERROR); else source.sendError(Messages.JSON_PARSING_ERROR);
				e.printStackTrace();
			}
			return null;
		}).thenAccept(body -> printNetworth(body, source, session.getUuid(), session.getUsername()));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static volatile String name = null;
	private static volatile String uuid = null;
	private static volatile boolean shouldSkip = false;
	
	private static int handleCommand(FabricClientCommandSource source, String player) {
		if(StringUtils.isBlank(Config.key)) {
			source.sendFeedback(Messages.NO_API_KEY_ERROR);
			return Command.SINGLE_SUCCESS;
		}
		
		CompletableFuture.supplyAsync(() -> {
			try {
				String response = Http.sendNameToUuidRequest(player);
				JsonObject json = JsonParser.parseString(response).getAsJsonObject();
				name = json.get("name").getAsString();
				uuid = json.get("id").getAsString();
			} catch (Exception e) {
				source.sendError(Messages.NAME_TO_UUID_ERROR);
				shouldSkip = true;
				e.printStackTrace();
			}
			return null;
		})
		.thenApply(x -> {
			try {
				return Http.sendHypixelRequest("skyblock/profiles", "&uuid=" + uuid, true, shouldSkip);
			} catch (Exception e) {
				source.sendError(Messages.SKYBLOCK_PROFILES_FETCH_ERROR);
				e.printStackTrace();
			}
			return null;
		})
		.thenApply(body -> {
			try {
				return Skyblock.getSelectedProfile2(body);
			} catch (Exception e) {
				if(e instanceof IllegalStateException) source.sendError(Messages.PROFILES_NOT_MIGRATED_ERROR); else source.sendError(Messages.JSON_PARSING_ERROR);
				e.printStackTrace();
			}
			return null;
		}).thenAccept(body -> printNetworth(body, source, uuid, name));
		
		return Command.SINGLE_SUCCESS;
	}
	
	public record Networth(long accessoriesValue, long armourValue, long bankValue, long enderchestValue, long inventoryValue, long overallValue, long petsValue, long purseValue, long sacksValue, long storageValue, long wardrobeValue) {
	}
		
	private static void printNetworth(JsonObject body, FabricClientCommandSource source, String uuid, String name) {
		NetworthCommand.name = null;
		NetworthCommand.uuid = null;
		NetworthCommand.shouldSkip = false;
		if(body == null) {
			return;
		}
		JsonObject profile = body.get("members").getAsJsonObject().get(uuid).getAsJsonObject();
		
		String endSpaces = "        " + name.replaceAll("[A-z0-9_]", "  ") + "        ";

		boolean inventoryEnabled = (profile.get("inv_contents") != null) ? true : false;
		long purse = (profile.get("coin_purse") != null) ? profile.get("coin_purse").getAsLong() : 0L;
		long bank = (body.get("banking") != null) ? body.get("banking").getAsJsonObject().get("balance").getAsLong() : 0L;
		
		JsonObject networthPostObject = new JsonObject();
		networthPostObject.add("data", profile);
		String networthPostBody = networthPostObject.toString();
		
		String networthData;
		Networth networth = null;
		if(inventoryEnabled == true) {
			try {
				networthData = Http.sendNetworthRequest(networthPostBody);
				networth = Skyblock.readNetworthData(networthData, bank, purse);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				source.sendError(NETWORTH_FETCH_ERROR);
				return;
			}
		} else {
			source.sendError(Messages.INVENTORY_API_DISABLED_ERROR);
			return;
		}
		
		source.sendFeedback(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true)))));
		
		source.sendFeedback(Text.literal("Networth » " + Functions.NUMBER_FORMATTER.format(networth.overallValue())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal(""));
		source.sendFeedback(Text.literal("Armour » " + Functions.NUMBER_FORMATTER.format(networth.armourValue())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Inventory » " + Functions.NUMBER_FORMATTER.format(networth.inventoryValue())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Enderchest » " + Functions.NUMBER_FORMATTER.format(networth.enderchestValue())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Storage » " + Functions.NUMBER_FORMATTER.format(networth.storageValue())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Accessories » " + Functions.NUMBER_FORMATTER.format(networth.accessoriesValue())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Pets » " + Functions.NUMBER_FORMATTER.format(networth.petsValue())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Wardrobe » " + Functions.NUMBER_FORMATTER.format(networth.wardrobeValue())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Sacks » " + Functions.NUMBER_FORMATTER.format(networth.sacksValue())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal(""));
		source.sendFeedback(Text.literal("Bank » " + Functions.NUMBER_FORMATTER.format(networth.bankValue())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Purse » " + Functions.NUMBER_FORMATTER.format(networth.purseValue())).styled(style -> style.withColor(colourProfile.infoColour)));
		
		source.sendFeedback(Text.literal(endSpaces).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
		return;
	}
}

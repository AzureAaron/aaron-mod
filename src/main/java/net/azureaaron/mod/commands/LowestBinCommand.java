package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Http;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LowestBinCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		final LiteralCommandNode<FabricClientCommandSource> lowestBinCommand = dispatcher.register(literal("lowestbin")
				.then(argument("item", greedyString())
						.suggests((context, builder) -> CommandSource.suggestMatching(Cache.ITEMS_LIST, builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "item")))));
		
		dispatcher.register(literal("lbin").redirect(lowestBinCommand));
	}
	
	private static final Text LOWEST_BIN_FETCH_ERROR = Text.literal("There was an error while fetching information for the lowest bin prices!").styled(style -> style.withColor(Formatting.RED));
	private static final Text DAY_AVERAGE_FETCH_ERROR = Text.literal("There was an error while fetching information for the average day price!").styled(style -> style.withColor(Formatting.RED));
	private static final Text NON_EXISTENT_ITEM_ERROR = Text.literal("The item you've provided is non existent!").styled(style -> style.withColor(Formatting.RED));
	private static final Text NO_AVERAGE_PRICE_FOR_ITEM_ERROR = Text.literal("No average price was found! (Most likely because this item hasn't been on the auction house recently!)").styled(style -> style.withColor(Formatting.RED));
	
	private static int handleCommand(final FabricClientCommandSource source, final String item) {
		String itemId = Cache.ITEM_NAMES.get(item);
		if(!Cache.ITEMS_LIST.contains(item)) {
			source.sendError(NON_EXISTENT_ITEM_ERROR);
			return Command.SINGLE_SUCCESS;
		}
		
		int average;
		String averageDescription;
		
		switch(Config.dayAverage) {
		case ONE_DAY:
			average = 1;
			averageDescription = "1 Day Avg.";
			break;
		case THREE_DAY:
			average = 3;
			averageDescription = "3 Day Avg.";
			break;
		case SEVEN_DAY:
			average = 7;
			averageDescription = "7 Day Avg.";
			break;
		default:
			average = 3;
			averageDescription = "3 Day Avg.";
		}
		
		CompletableFuture.supplyAsync(() -> {
			try {
				String lowestBinResponse = Http.sendMoulberryRequest("lowestbin.json");
				JsonObject lowestBin = JsonParser.parseString(lowestBinResponse).getAsJsonObject();	
				JsonObject priceObject = new JsonObject();
				
				priceObject.addProperty("price", lowestBin.get(itemId).getAsLong());
				return priceObject;
			} catch (Exception e) {
				source.sendError(LOWEST_BIN_FETCH_ERROR);
				e.printStackTrace();
			}
			return null;
		}).thenApply(itemPrice -> {
			if(itemPrice == null) return null;
			//TODO proper error msg for when there is no day average for an item (bc it hasn't been auctioned recently)
			try {
				String dayAverageResponse = Http.sendMoulberryRequest("auction_averages_lbin/" + String.valueOf(average) + "day.json");
				JsonObject dayAverage = JsonParser.parseString(dayAverageResponse).getAsJsonObject();
				
				if(dayAverage.get(itemId) == null) { //Does this even work?
					source.sendError(NO_AVERAGE_PRICE_FOR_ITEM_ERROR);
					return null;
				}
								
				JsonObject response = new JsonObject();
				response.addProperty("price", itemPrice.get("price").getAsLong());
				response.addProperty("dayAverage", dayAverage.get(itemId).getAsLong());
				return response;
			} catch (Exception e) {
				source.sendError(DAY_AVERAGE_FETCH_ERROR);
				e.printStackTrace();
			}
			return null;
		})
		.thenAccept(data -> {
			printLowestBin(source, data, item, averageDescription);
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static void printLowestBin(FabricClientCommandSource source, JsonObject data, String itemName, String desc) {
		if(data == null) return;
		final String endSpaces = "        " + itemName.replaceAll("[A-z0-9_()']", "  ") + "        ";
		
		source.sendFeedback(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(itemName).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true)))));
		
		source.sendFeedback(Text.literal("Lowest BIN Price » " + Functions.NUMBER_FORMATTER_ND.format(data.get("price").getAsLong())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal(""));
		source.sendFeedback(Text.literal(desc + " Price » " + Functions.NUMBER_FORMATTER_ND.format(data.get("dayAverage").getAsLong())).styled(style -> style.withColor(colourProfile.infoColour)));
		
		source.sendFeedback(Text.literal(endSpaces).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
		return;
	}
}

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

import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.Messages;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BazaarCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		final LiteralCommandNode<FabricClientCommandSource> bazaarCommand = dispatcher.register(literal("bazaar")
				.then(argument("product", greedyString())
						.suggests((context, builder) -> CommandSource.suggestMatching(Cache.PRODUCTS_LIST, builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "product")))));
		
		dispatcher.register(literal("bz").redirect(bazaarCommand));
	}
	
	private static final Text NON_EXISTENT_PRODUCT_ERROR = Text.literal("The product you've provided is non existent!").styled(style -> style.withColor(Formatting.RED));
	private static final Text BAZAAR_FETCH_ERROR = Text.literal("There was an error while fetching information from the bazaar!").styled(style -> style.withColor(Formatting.RED));
	
	private static int handleCommand(FabricClientCommandSource source, String product) {
		
		if(!Cache.PRODUCTS_LIST.contains(product)) {
			source.sendError(NON_EXISTENT_PRODUCT_ERROR);
			return Command.SINGLE_SUCCESS;
		}
		
		CompletableFuture.supplyAsync(() -> {
			try {
				String response = Http.sendUnauthorizedHypixelRequest("skyblock/bazaar", "");
				JsonObject data = JsonParser.parseString(response).getAsJsonObject();
				
				return data.get("products").getAsJsonObject().get(Cache.PRODUCTS_MAP.get(product)).getAsJsonObject().get("quick_status").getAsJsonObject();
			} catch (Exception e) {
				source.sendError(BAZAAR_FETCH_ERROR);
				e.printStackTrace();
			}
			return null;
		})
		.thenAccept(productData -> {
			if (productData != null) {
				try {
					printBazaar(source, productData, product);
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR);
					t.printStackTrace();
				}
			}
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static void printBazaar(FabricClientCommandSource source, JsonObject productData, String productName) {
		final String endSpaces = "        " + productName.replaceAll("[A-z0-9_()']", "  ") + "        ";
		
		source.sendFeedback(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(productName).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true)))));
		
		source.sendFeedback(Text.literal("Buy Price » " + Functions.NUMBER_FORMATTER.format(productData.get("buyPrice").getAsDouble())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("buyVolume").getAsInt()) + " in " + Functions.NUMBER_FORMATTER_S.format(productData.get("buyOrders").getAsInt()) + " offers").styled(style -> style.withColor(colourProfile.supportingInfoColour)));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("buyMovingWeek").getAsInt()) + " insta-buys in 7 days").styled(style -> style.withColor(colourProfile.supportingInfoColour)));
		
		source.sendFeedback(Text.literal(""));
		
		source.sendFeedback(Text.literal("Sell Price » " + Functions.NUMBER_FORMATTER.format(productData.get("sellPrice").getAsDouble())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("sellVolume").getAsInt()) + " in " + Functions.NUMBER_FORMATTER_S.format(productData.get("sellOrders").getAsInt()) + " orders").styled(style -> style.withColor(colourProfile.supportingInfoColour)));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("sellMovingWeek").getAsInt()) + " insta-sells in 7 days").styled(style -> style.withColor(colourProfile.supportingInfoColour)));
		
		source.sendFeedback(Text.literal(endSpaces).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
		return;
	}
}

package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.logging.LogUtils;

import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.Messages;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BazaarCommand {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text NON_EXISTENT_PRODUCT_ERROR = Text.literal("The product you've provided is non existent!").formatted(Formatting.RED);
	private static final Text BAZAAR_FETCH_ERROR = Text.literal("There was an error while fetching information from the bazaar!").formatted(Formatting.RED);
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		final LiteralCommandNode<FabricClientCommandSource> bazaarCommand = dispatcher.register(literal("bazaarprice")
				.then(argument("product", greedyString())
						.suggests((context, builder) -> CommandSource.suggestMatching(Cache.PRODUCTS_LIST, builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "product")))));
		
		dispatcher.register(literal("bzprice").redirect(bazaarCommand));
	}
	
	private static int handleCommand(FabricClientCommandSource source, String product) {
		
		//TODO lazy load this shit
		if (!Cache.PRODUCTS_LIST.contains(product)) {
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
				LOGGER.error("[Aaron's Mod] Failed to load bazaar price data!", e);
			}
			return null;
		})
		.thenAccept(productData -> {
			if (productData != null) {
				try {
					printBazaar(source, productData, product);
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR);
					LOGGER.error("[Aaron's Mod] Encountered an unknown error while executing the /bazaar command!", t);
				}
			}
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static void printBazaar(FabricClientCommandSource source, JsonObject productData, String productName) {		
		Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(productName).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true))));
		
		source.sendFeedback(startText);
		
		source.sendFeedback(Text.literal("Buy Price » " + Functions.NUMBER_FORMATTER.format(productData.get("buyPrice").getAsDouble())).withColor(colourProfile.infoColour));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("buyVolume").getAsInt()) + " in " + Functions.NUMBER_FORMATTER_S.format(productData.get("buyOrders").getAsInt()) + " offers").withColor(colourProfile.supportingInfoColour));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("buyMovingWeek").getAsInt()) + " insta-buys in 7 days").withColor(colourProfile.supportingInfoColour));
		
		source.sendFeedback(Text.literal(""));
		
		source.sendFeedback(Text.literal("Sell Price » " + Functions.NUMBER_FORMATTER.format(productData.get("sellPrice").getAsDouble())).withColor(colourProfile.infoColour));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("sellVolume").getAsInt()) + " in " + Functions.NUMBER_FORMATTER_S.format(productData.get("sellOrders").getAsInt()) + " orders").withColor(colourProfile.supportingInfoColour));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("sellMovingWeek").getAsInt()) + " insta-sells in 7 days").withColor(colourProfile.supportingInfoColour));
		
		source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
	}
}

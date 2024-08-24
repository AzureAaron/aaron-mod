package net.azureaaron.mod.commands.skyblock;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.logging.LogUtils;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.commands.CommandSystem;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.Http;
import net.azureaaron.mod.utils.Messages;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BazaarCommand {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Supplier<MutableText> NON_EXISTENT_PRODUCT_ERROR = () -> Constants.PREFIX.get().append(Text.literal("The product you've provided is non existent!").formatted(Formatting.RED));
	private static final Supplier<MutableText> BAZAAR_FETCH_ERROR = () -> Constants.PREFIX.get().append(Text.literal("There was an error while fetching information from the bazaar!").formatted(Formatting.RED));
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		final LiteralCommandNode<FabricClientCommandSource> bazaarCommand = dispatcher.register(literal("bazaarprice")
				.then(argument("product", greedyString())
						.suggests((context, builder) -> CommandSource.suggestMatching(Cache.PRODUCTS_LIST, builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "product")))));
		
		dispatcher.register(literal("bzprice").redirect(bazaarCommand));
	}
	
	private static int handleCommand(FabricClientCommandSource source, String product) {
		
		//TODO lazy load this shit
		if (!Cache.PRODUCTS_LIST.contains(product)) {
			source.sendError(NON_EXISTENT_PRODUCT_ERROR.get());
			
			return Command.SINGLE_SUCCESS;
		}
		
		CompletableFuture.supplyAsync(() -> {
			try {
				String response = Http.sendGetRequest("https://api.hypixel.net/v2/skyblock/bazaar");
				JsonObject data = JsonParser.parseString(response).getAsJsonObject();
				
				return data.get("products").getAsJsonObject().get(Cache.PRODUCTS_MAP.get(product)).getAsJsonObject().get("quick_status").getAsJsonObject();
			} catch (Exception e) {
				source.sendError(BAZAAR_FETCH_ERROR.get());
				LOGGER.error("[Aaron's Mod] Failed to load bazaar price data!", e);
			}
			return null;
		})
		.thenAccept(productData -> {
			if (productData != null) {
				try {
					printBazaar(source, productData, product);
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR.get());
					LOGGER.error("[Aaron's Mod] Encountered an unknown error while executing the /bazaar command!", t);
				}
			}
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static void printBazaar(FabricClientCommandSource source, JsonObject productData, String productName) {
		ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
		
		Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
				.append(Text.literal(productName).styled(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt())).styled(style -> style.withStrikethrough(true))));
		
		source.sendFeedback(startText);
		
		source.sendFeedback(Text.literal("Buy Price » " + Functions.NUMBER_FORMATTER.format(productData.get("buyPrice").getAsDouble())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("buyVolume").getAsInt()) + " in " + Functions.NUMBER_FORMATTER_S.format(productData.get("buyOrders").getAsInt()) + " offers").withColor(colourProfile.supportingInfoColour.getAsInt()));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("buyMovingWeek").getAsInt()) + " insta-buys in 7 days").withColor(colourProfile.supportingInfoColour.getAsInt()));
		
		source.sendFeedback(Text.literal(""));
		
		source.sendFeedback(Text.literal("Sell Price » " + Functions.NUMBER_FORMATTER.format(productData.get("sellPrice").getAsDouble())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("sellVolume").getAsInt()) + " in " + Functions.NUMBER_FORMATTER_S.format(productData.get("sellOrders").getAsInt()) + " orders").withColor(colourProfile.supportingInfoColour.getAsInt()));
		source.sendFeedback(Text.literal(Functions.NUMBER_FORMATTER_S.format(productData.get("sellMovingWeek").getAsInt()) + " insta-sells in 7 days").withColor(colourProfile.supportingInfoColour.getAsInt()));
		
		source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
	}
}

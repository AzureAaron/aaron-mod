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
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.commands.CommandSystem;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.Formatters;
import net.azureaaron.mod.utils.Http;
import net.azureaaron.mod.utils.Messages;
import net.azureaaron.mod.utils.render.RenderHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LowestBinCommand {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Supplier<MutableText> LOWEST_BIN_FETCH_ERROR = () -> Constants.PREFIX.get().append(Text.literal("There was an error while fetching information for the lowest bin prices!").formatted(Formatting.RED));
	private static final Supplier<MutableText> DAY_AVERAGE_FETCH_ERROR = () -> Constants.PREFIX.get().append(Text.literal("There was an error while fetching information for the average day price!").formatted(Formatting.RED));
	private static final Supplier<MutableText> NON_EXISTENT_ITEM_ERROR = () -> Constants.PREFIX.get().append(Text.literal("The item you've provided is non existent!").formatted(Formatting.RED));
	private static final Supplier<MutableText> NO_AVERAGE_PRICE_FOR_ITEM_ERROR = () -> Constants.PREFIX.get().append(Text.literal("No average price was found! (Most likely because this item hasn't been on the auction house recently!)").formatted(Formatting.RED));

	@Init
	public static void init() {
		if (AaronModConfigManager.get().skyblock.commands.enableSkyblockCommands) ClientCommandRegistrationCallback.EVENT.register(LowestBinCommand::register);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		final LiteralCommandNode<FabricClientCommandSource> lowestBinCommand = dispatcher.register(literal("lowestbin")
				.then(argument("item", greedyString())
						.suggests((context, builder) -> CommandSource.suggestMatching(Cache.ITEMS_LIST, builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "item")))));

		dispatcher.register(literal("lbin").redirect(lowestBinCommand));
	}

	private static int handleCommand(FabricClientCommandSource source, String item) {
		String itemId = Cache.ITEM_NAMES.get(item);

		if (!Cache.ITEMS_LIST.contains(item)) {
			source.sendError(NON_EXISTENT_ITEM_ERROR.get());

			return Command.SINGLE_SUCCESS;
		}

		int average;
		String averageDescription;

		switch (AaronModConfigManager.get().skyblock.commands.lbinPriceDayAverage) {
			case ONE_DAY -> {
				average = 1;
				averageDescription = "1 Day Avg.";
			}
			case THREE_DAY -> {
				average = 3;
				averageDescription = "3 Day Avg.";
			}
			case SEVEN_DAY -> {
				average = 7;
				averageDescription = "7 Day Avg.";
			}
			default -> {
				average = 3;
				averageDescription = "3 Day Avg.";
			}
		}

		CompletableFuture.supplyAsync(() -> {
			try {
				String lowestBinResponse = Http.sendGetRequest("https://moulberry.codes/lowestbin.json");
				JsonObject lowestBin = JsonParser.parseString(lowestBinResponse).getAsJsonObject();
				JsonObject priceObject = new JsonObject();

				priceObject.addProperty("price", lowestBin.get(itemId).getAsLong());

				return priceObject;
			} catch (Exception e) {
				source.sendError(LOWEST_BIN_FETCH_ERROR.get());
				LOGGER.error("[Aaron's Mod] Encountered an exception while fetching lbin prices!", e);
			}

			return null;
		})
		.thenApply(itemPrice -> {
			if (itemPrice == null) return null;

			//TODO proper error msg for when there is no day average for an item (bc it hasn't been auctioned recently)
			try {
				String dayAverageResponse = Http.sendGetRequest("https://moulberry.codes/auction_averages_lbin/" + average + "day.json");
				JsonObject dayAverage = JsonParser.parseString(dayAverageResponse).getAsJsonObject();

				if (dayAverage.get(itemId) == null) { //Does this even work?
					source.sendError(NO_AVERAGE_PRICE_FOR_ITEM_ERROR.get());

					return null;
				}

				JsonObject response = new JsonObject();
				response.addProperty("price", itemPrice.get("price").getAsLong());
				response.addProperty("dayAverage", dayAverage.get(itemId).getAsLong());

				return response;
			} catch (Exception e) {
				source.sendError(DAY_AVERAGE_FETCH_ERROR.get());
				LOGGER.error("[Aaron's Mod] Encountered an exception while fetching lbin day average prices!", e);
			}

			return null;
		})
		.thenAccept(data -> {
			if (data != null) {
				try {
					printLowestBin(source, data, item, averageDescription);
				} catch (Exception e) {
					source.sendError(Messages.UNKNOWN_ERROR.get());
					LOGGER.error("[Aaron's Mod] Encountered an exception while printing lbin feedback messages!", e);
				}
			}
		});

		return Command.SINGLE_SUCCESS;
	}

	private static void printLowestBin(FabricClientCommandSource source, JsonObject data, String itemName, String desc) {
		RenderHelper.runOnRenderThread(() -> {
			ColourProfiles colourProfile = Constants.PROFILE.get();

			Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
					.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
					.append(Text.literal(itemName).styled(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
					.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
					.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt())).styled(style -> style.withStrikethrough(true))));

			source.sendFeedback(startText);

			source.sendFeedback(Text.literal("Lowest BIN Price » " + Formatters.INTEGER_NUMBERS.format(data.get("price").getAsLong())).withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Text.literal(""));
			source.sendFeedback(Text.literal(desc + " Price » " + Formatters.INTEGER_NUMBERS.format(data.get("dayAverage").getAsLong())).withColor(colourProfile.infoColour.getAsInt()));

			source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
		});
	}
}

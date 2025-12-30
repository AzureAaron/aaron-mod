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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class BazaarCommand {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Supplier<MutableComponent> NON_EXISTENT_PRODUCT_ERROR = () -> Constants.PREFIX.get().append(Component.literal("The product you've provided is non existent!").withStyle(ChatFormatting.RED));
	private static final Supplier<MutableComponent> BAZAAR_FETCH_ERROR = () -> Constants.PREFIX.get().append(Component.literal("There was an error while fetching information from the bazaar!").withStyle(ChatFormatting.RED));

	@Init
	public static void init() {
		if (AaronModConfigManager.get().skyblock.commands.enableSkyblockCommands) ClientCommandRegistrationCallback.EVENT.register(BazaarCommand::register);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		final LiteralCommandNode<FabricClientCommandSource> bazaarCommand = dispatcher.register(literal("bazaarprice")
				.then(argument("product", greedyString())
						.suggests((context, builder) -> SharedSuggestionProvider.suggest(Cache.PRODUCTS_LIST, builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "product")))));

		dispatcher.register(literal("bzprice").redirect(bazaarCommand));
	}

	private static int handleCommand(FabricClientCommandSource source, String product) {
		//TODO lazy load this
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
		RenderHelper.runOnRenderThread(() -> {
			ColourProfiles colourProfile = Constants.PROFILE.get();

			Component startText = Component.literal("     ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
					.append(Component.literal("[- ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
					.append(Component.literal(productName).withStyle(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
					.append(Component.literal(" -]").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
					.append(Component.literal("     ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt())).withStyle(style -> style.withStrikethrough(true))));

			source.sendFeedback(startText);

			source.sendFeedback(Component.literal("Buy Price » " + Formatters.DOUBLE_NUMBERS.format(productData.get("buyPrice").getAsDouble())).withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Component.literal(Formatters.SHORT_INTEGER_NUMBERS.format(productData.get("buyVolume").getAsInt()) + " in " + Formatters.SHORT_INTEGER_NUMBERS.format(productData.get("buyOrders").getAsInt()) + " offers").withColor(colourProfile.supportingInfoColour.getAsInt()));
			source.sendFeedback(Component.literal(Formatters.SHORT_INTEGER_NUMBERS.format(productData.get("buyMovingWeek").getAsInt()) + " insta-buys in 7 days").withColor(colourProfile.supportingInfoColour.getAsInt()));

			source.sendFeedback(Component.literal(""));

			source.sendFeedback(Component.literal("Sell Price » " + Formatters.DOUBLE_NUMBERS.format(productData.get("sellPrice").getAsDouble())).withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Component.literal(Formatters.SHORT_INTEGER_NUMBERS.format(productData.get("sellVolume").getAsInt()) + " in " + Formatters.SHORT_INTEGER_NUMBERS.format(productData.get("sellOrders").getAsInt()) + " orders").withColor(colourProfile.supportingInfoColour.getAsInt()));
			source.sendFeedback(Component.literal(Formatters.SHORT_INTEGER_NUMBERS.format(productData.get("sellMovingWeek").getAsInt()) + " insta-sells in 7 days").withColor(colourProfile.supportingInfoColour.getAsInt()));

			source.sendFeedback(Component.literal(CommandSystem.getEndSpaces(startText)).withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
		});
	}
}

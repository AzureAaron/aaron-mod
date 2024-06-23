package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.lang.invoke.MethodHandle;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.Http;
import net.azureaaron.mod.utils.JsonHelper;
import net.azureaaron.mod.utils.Messages;
import net.azureaaron.mod.utils.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NetworthCommand {
	private static final Logger LOGGER = LogUtils.getLogger();
	static final MethodHandle DISPATCH_HANDLE = CommandSystem.obtainDispatchHandle4Skyblock("printNetworth");
	private static final Supplier<MutableText> NETWORTH_FETCH_ERROR = () -> Constants.PREFIX.get().append(Text.literal("There was an error while fetching a player's networth!").formatted(Formatting.RED));
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("networth")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
		
		dispatcher.register(literal("nw")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
	}
		
	public record Networth(long accessoriesValue, long armourValue, long bankValue, long enderchestValue, long inventoryValue, long overallValue, long petsValue, long purseValue, long sacksValue, long storageValue, long wardrobeValue) {}
		
	protected static void printNetworth(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
		
		JsonObject profile = body.getAsJsonObject("members").getAsJsonObject(uuid);
		
		boolean inventoryEnabled = profile.has("inv_contents");
		long purse = JsonHelper.getLong(profile, "coin_purse").orElse(0L);
		long bank = JsonHelper.getLong(body, "banking.balance").orElse(0L);
		
		JsonObject networthPostObject = new JsonObject();
		networthPostObject.add("data", profile);
		String networthPostBody = networthPostObject.toString();
		
		String networthData;
		Networth networth = null;
		if (inventoryEnabled == true) {
			try {
				networthData = Http.sendPostRequest(networthPostBody, Http.NETWORTH, "application/json");
				networth = Skyblock.readNetworthData(networthData, bank, purse);
			} catch (Throwable t) {
				source.sendError(NETWORTH_FETCH_ERROR.get());
				LOGGER.error("[Aaron's Mod] Encountered an exception while requesting networth data!", t);
				
				return;
			}
		} else {
			source.sendError(Messages.INVENTORY_API_DISABLED_ERROR.get());
			
			return;
		}
		
		Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt())).styled(style -> style.withStrikethrough(true))));
		
		source.sendFeedback(startText);
		
		source.sendFeedback(Text.literal("Networth » " + Functions.NUMBER_FORMATTER.format(networth.overallValue())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal(""));
		source.sendFeedback(Text.literal("Armour » " + Functions.NUMBER_FORMATTER.format(networth.armourValue())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Inventory » " + Functions.NUMBER_FORMATTER.format(networth.inventoryValue())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Enderchest » " + Functions.NUMBER_FORMATTER.format(networth.enderchestValue())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Storage » " + Functions.NUMBER_FORMATTER.format(networth.storageValue())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Accessories » " + Functions.NUMBER_FORMATTER.format(networth.accessoriesValue())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Pets » " + Functions.NUMBER_FORMATTER.format(networth.petsValue())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Wardrobe » " + Functions.NUMBER_FORMATTER.format(networth.wardrobeValue())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Sacks » " + Functions.NUMBER_FORMATTER.format(networth.sacksValue())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal(""));
		source.sendFeedback(Text.literal("Bank » " + Functions.NUMBER_FORMATTER.format(networth.bankValue())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Purse » " + Functions.NUMBER_FORMATTER.format(networth.purseValue())).withColor(colourProfile.infoColour.getAsInt()));
		
		source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
	}
}

package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.lang.invoke.MethodHandle;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;

import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.JsonHelper;
import net.azureaaron.mod.util.Messages;
import net.azureaaron.mod.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NetworthCommand {
	private static final Logger LOGGER = LogUtils.getLogger();
	static final MethodHandle DISPATCH_HANDLE = CommandSystem.obtainDispatchHandle4Skyblock("printNetworth");
	private static final Text NETWORTH_FETCH_ERROR = Text.literal("There was an error while fetching a player's networth!").styled(style -> style.withColor(Formatting.RED));
	
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
				networthData = Http.sendNetworthRequest(networthPostBody);
				networth = Skyblock.readNetworthData(networthData, bank, purse);
			} catch (Throwable t) {
				source.sendError(NETWORTH_FETCH_ERROR);
				LOGGER.error("[Aaron's Mod] Encountered an exception while requesting networth data!", t);
				
				return;
			}
		} else {
			source.sendError(Messages.INVENTORY_API_DISABLED_ERROR);
			
			return;
		}
		
		Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true))));
		
		source.sendFeedback(startText);
		
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
		
		source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
	}
}

package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.lang.invoke.MethodHandle;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.JsonHelper;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class EssenceCommand {
	private static final MethodHandle DISPATCH_HANDLE = CommandSystem.obtainDispatchHandle4Skyblock("printEssence");
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("essence")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
	}
	
	protected static void printEssence(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		JsonObject profile = body.getAsJsonObject("members").getAsJsonObject(uuid);
		
		JsonObject currencies = profile.getAsJsonObject("currencies");
		
		int witherEssence = JsonHelper.getInt(currencies, "essence.WITHER.current").orElse(0);
		int spiderEssence = JsonHelper.getInt(currencies, "essence.SPIDER.current").orElse(0);
		int undeadEssence = JsonHelper.getInt(currencies, "essence.UNDEAD.current").orElse(0);
		int dragonEssence = JsonHelper.getInt(currencies, "essence.DRAGON.current").orElse(0);
		int goldEssence = JsonHelper.getInt(currencies, "essence.GOLD.current").orElse(0);
		int diamondEssence = JsonHelper.getInt(currencies, "essence.DIAMOND.current").orElse(0);
		int iceEssence = JsonHelper.getInt(currencies, "essence.ICE.current").orElse(0);
		int crimsonEssence = JsonHelper.getInt(currencies, "essence.CRIMSON.current").orElse(0);
		
		Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true))));
		
		source.sendFeedback(startText);
		
		source.sendFeedback(Text.literal("Wither » " + Functions.NUMBER_FORMATTER_ND.format(witherEssence)).withColor(colourProfile.infoColour));
		source.sendFeedback(Text.literal("Spider » " + Functions.NUMBER_FORMATTER_ND.format(spiderEssence)).withColor(colourProfile.infoColour));
		source.sendFeedback(Text.literal("Undead » " + Functions.NUMBER_FORMATTER_ND.format(undeadEssence)).withColor(colourProfile.infoColour));
		source.sendFeedback(Text.literal("Dragon » " + Functions.NUMBER_FORMATTER_ND.format(dragonEssence)).withColor(colourProfile.infoColour));
		source.sendFeedback(Text.literal("Gold » " + Functions.NUMBER_FORMATTER_ND.format(goldEssence)).withColor(colourProfile.infoColour));
		source.sendFeedback(Text.literal("Diamond » " + Functions.NUMBER_FORMATTER_ND.format(diamondEssence)).withColor(colourProfile.infoColour));
		source.sendFeedback(Text.literal("Ice » " + Functions.NUMBER_FORMATTER_ND.format(iceEssence)).withColor(colourProfile.infoColour));
		source.sendFeedback(Text.literal("Crimson » " + Functions.NUMBER_FORMATTER_ND.format(crimsonEssence)).withColor(colourProfile.infoColour));
		
		source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
	}
}

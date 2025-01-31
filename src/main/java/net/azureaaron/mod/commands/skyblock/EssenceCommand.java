package net.azureaaron.mod.commands.skyblock;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.commands.Command;
import net.azureaaron.mod.commands.CommandSystem;
import net.azureaaron.mod.commands.SkyblockCommand;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Formatters;
import net.azureaaron.mod.utils.JsonHelper;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class EssenceCommand extends SkyblockCommand {
	public static final Command INSTANCE = new EssenceCommand();

	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(literal("essence")
				.executes(context -> CommandSystem.handleSelf4Skyblock(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(this, context.getSource(), getString(context, "player")))));
	}

	@Override
	public void print(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
		
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
		
		Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt())).styled(style -> style.withStrikethrough(true))));
		
		source.sendFeedback(startText);
		
		source.sendFeedback(Text.literal("Wither » " + Formatters.INTEGER_NUMBERS.format(witherEssence)).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Spider » " + Formatters.INTEGER_NUMBERS.format(spiderEssence)).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Undead » " + Formatters.INTEGER_NUMBERS.format(undeadEssence)).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Dragon » " + Formatters.INTEGER_NUMBERS.format(dragonEssence)).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Gold » " + Formatters.INTEGER_NUMBERS.format(goldEssence)).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Diamond » " + Formatters.INTEGER_NUMBERS.format(diamondEssence)).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Ice » " + Formatters.INTEGER_NUMBERS.format(iceEssence)).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Crimson » " + Formatters.INTEGER_NUMBERS.format(crimsonEssence)).withColor(colourProfile.infoColour.getAsInt()));
		
		source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
	}
}

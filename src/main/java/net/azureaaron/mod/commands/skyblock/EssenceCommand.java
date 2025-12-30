package net.azureaaron.mod.commands.skyblock;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.commands.Command;
import net.azureaaron.mod.commands.CommandSystem;
import net.azureaaron.mod.commands.SkyblockCommand;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.Formatters;
import net.azureaaron.mod.utils.JsonHelper;
import net.azureaaron.mod.utils.render.RenderHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class EssenceCommand extends SkyblockCommand {
	private static final Command INSTANCE = new EssenceCommand();

	@Init
	public static void init() {
		if (AaronModConfigManager.get().skyblock.commands.enableSkyblockCommands) ClientCommandRegistrationCallback.EVENT.register(INSTANCE::register);
	}

	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("essence")
				.executes(context -> CommandSystem.handleSelf4Skyblock(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> SharedSuggestionProvider.suggest(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(this, context.getSource(), getString(context, "player")))));
	}

	@Override
	public void print(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		ColourProfiles colourProfile = Constants.PROFILE.get();

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

		RenderHelper.runOnRenderThread(() -> {
			Component startText = Component.literal("     ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
					.append(Component.literal("[- ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
					.append(Component.literal(name).withStyle(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
					.append(Component.literal(" -]").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
					.append(Component.literal("     ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt())).withStyle(style -> style.withStrikethrough(true))));

			source.sendFeedback(startText);

			source.sendFeedback(Component.literal("Wither » " + Formatters.INTEGER_NUMBERS.format(witherEssence)).withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Component.literal("Spider » " + Formatters.INTEGER_NUMBERS.format(spiderEssence)).withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Component.literal("Undead » " + Formatters.INTEGER_NUMBERS.format(undeadEssence)).withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Component.literal("Dragon » " + Formatters.INTEGER_NUMBERS.format(dragonEssence)).withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Component.literal("Gold » " + Formatters.INTEGER_NUMBERS.format(goldEssence)).withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Component.literal("Diamond » " + Formatters.INTEGER_NUMBERS.format(diamondEssence)).withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Component.literal("Ice » " + Formatters.INTEGER_NUMBERS.format(iceEssence)).withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Component.literal("Crimson » " + Formatters.INTEGER_NUMBERS.format(crimsonEssence)).withColor(colourProfile.infoColour.getAsInt()));

			source.sendFeedback(Component.literal(CommandSystem.getEndSpaces(startText)).withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
		});
	}
}

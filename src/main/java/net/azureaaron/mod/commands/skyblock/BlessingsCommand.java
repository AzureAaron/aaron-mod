package net.azureaaron.mod.commands.skyblock;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Constants;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

public class BlessingsCommand {

	@Init
	public static void init() {
		if (AaronModConfigManager.get().skyblock.commands.enableSkyblockCommands) ClientCommandRegistrationCallback.EVENT.register(BlessingsCommand::register);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("blessings")
				.executes(context -> printBlessings(context.getSource()))
				.then(argument("option", word())
						.suggests((context, builder) -> builder.suggest("reset").buildFuture())
						.executes(context -> printBlessings(context.getSource(), getString(context, "option")))));
	}

	@SuppressWarnings("removal")
	private static int printBlessings(FabricClientCommandSource source) {
		ColourProfiles colourProfile = Constants.PROFILE.get();

		source.sendFeedback(Component.literal("               ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));

		source.sendFeedback(Component.literal("Power » " + Cache.powerBlessing).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Component.literal("Wisdom » " + Cache.wisdomBlessing).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Component.literal("Life » " + Cache.lifeBlessing).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Component.literal("Stone » " + Cache.stoneBlessing).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Component.literal("Time » " + (Cache.timeBlessing ? "✓" : "✗")).withColor(colourProfile.infoColour.getAsInt()));

		source.sendFeedback(Component.literal("               ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));

		return Command.SINGLE_SUCCESS;
	}

	private static int printBlessings(FabricClientCommandSource source, String option) {
		ColourProfiles colourProfile = Constants.PROFILE.get();

		if ("reset".equals(option)) {
			Cache.resetBlessings();
			source.sendFeedback(Constants.PREFIX.get().append(Component.literal("Blessings » ").withColor(colourProfile.primaryColour.getAsInt())
					.append(Component.literal("Successfully reset the counter!").withColor(colourProfile.secondaryColour.getAsInt()))));
		} else {
			source.sendError(Constants.PREFIX.get().append(Component.literal("Invalid option!").withStyle(ChatFormatting.RED)));
		}

		return Command.SINGLE_SUCCESS;
	}
}

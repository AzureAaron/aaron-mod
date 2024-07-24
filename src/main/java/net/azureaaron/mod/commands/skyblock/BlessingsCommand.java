package net.azureaaron.mod.commands.skyblock;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Constants;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BlessingsCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(literal("blessings")
				.executes(context -> printBlessings(context.getSource()))
				.then(argument("option", word())
						.suggests((context, builder) -> builder.suggest("reset").buildFuture())
						.executes(context -> printBlessings(context.getSource(), getString(context, "option")))));
	}
	
    @SuppressWarnings("removal")
	private static int printBlessings(FabricClientCommandSource source) {
    	ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
    	
    	source.sendFeedback(Text.literal("               ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
    	
    	source.sendFeedback(Text.literal("Power » " + Cache.powerBlessing).withColor(colourProfile.infoColour.getAsInt()));
    	source.sendFeedback(Text.literal("Wisdom » " + Cache.wisdomBlessing).withColor(colourProfile.infoColour.getAsInt()));
    	source.sendFeedback(Text.literal("Life » " + Cache.lifeBlessing).withColor(colourProfile.infoColour.getAsInt()));
    	source.sendFeedback(Text.literal("Stone » " + Cache.stoneBlessing).withColor(colourProfile.infoColour.getAsInt()));
    	source.sendFeedback(Text.literal("Time » " + (Cache.timeBlessing ? "✓" : "✗")).withColor(colourProfile.infoColour.getAsInt()));
    	
    	source.sendFeedback(Text.literal("               ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));

        return Command.SINGLE_SUCCESS;
    }
    
    private static int printBlessings(FabricClientCommandSource source, String option) {
    	ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
    	
    	if("reset".equals(option)) {
    		Cache.resetBlessings();
			source.sendFeedback(Constants.PREFIX.get().append(Text.literal("Blessings » ").withColor(colourProfile.primaryColour.getAsInt())
					.append(Text.literal("Successfully reset the counter!").withColor(colourProfile.secondaryColour.getAsInt()))));
    	} else {
    		source.sendError(Constants.PREFIX.get().append(Text.literal("Invalid option!").formatted(Formatting.RED)));
    	}
    	
    	return Command.SINGLE_SUCCESS;
    }
}

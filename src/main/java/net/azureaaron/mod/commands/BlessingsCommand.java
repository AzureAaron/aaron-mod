package net.azureaaron.mod.commands;

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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BlessingsCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("blessings")
				.executes(context -> printBlessings(context.getSource()))
				.then(argument("option", word())
						.suggests((context, builder) -> builder.suggest("reset").buildFuture())
						.executes(context -> printBlessings(context.getSource(), getString(context, "option")))));
	}
	
    private static int printBlessings(FabricClientCommandSource source) {
    	ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
    	
    	String powerBlessing = String.valueOf(Cache.powerBlessing);
    	String wisdomBlessing = String.valueOf(Cache.wisdomBlessing);
    	String lifeBlessing = String.valueOf(Cache.lifeBlessing);
    	String stoneBlessing = String.valueOf(Cache.stoneBlessing);
    	String timeBlessing = Cache.timeBlessing ? "✓" : "✗";
    	
    	source.sendFeedback(Text.literal("               ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
    	
    	source.sendFeedback(Text.literal("Power » " + powerBlessing).withColor(colourProfile.infoColour.getAsInt()));
    	source.sendFeedback(Text.literal("Wisdom » " + wisdomBlessing).withColor(colourProfile.infoColour.getAsInt()));
    	source.sendFeedback(Text.literal("Life » " + lifeBlessing).withColor(colourProfile.infoColour.getAsInt()));
    	source.sendFeedback(Text.literal("Stone » " + stoneBlessing).withColor(colourProfile.infoColour.getAsInt()));
    	source.sendFeedback(Text.literal("Time » " +  timeBlessing).withColor(colourProfile.infoColour.getAsInt()));
    	
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

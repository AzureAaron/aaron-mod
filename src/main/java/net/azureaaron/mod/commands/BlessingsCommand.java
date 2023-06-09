package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.util.Cache;
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
    	String powerBlessing = String.valueOf(Cache.powerBlessing);
    	String wisdomBlessing = String.valueOf(Cache.wisdomBlessing);
    	String lifeBlessing = String.valueOf(Cache.lifeBlessing);
    	String stoneBlessing = String.valueOf(Cache.stoneBlessing);
    	String timeBlessing = Cache.timeBlessing ? "✓" : "✗";
    	
    	source.sendFeedback(Text.literal("               ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
    	
    	source.sendFeedback(Text.literal("Power » " + powerBlessing).styled(style -> style.withColor(colourProfile.infoColour)));
    	source.sendFeedback(Text.literal("Wisdom » " + wisdomBlessing).styled(style -> style.withColor(colourProfile.infoColour)));
    	source.sendFeedback(Text.literal("Life » " + lifeBlessing).styled(style -> style.withColor(colourProfile.infoColour)));
    	source.sendFeedback(Text.literal("Stone » " + stoneBlessing).styled(style -> style.withColor(colourProfile.infoColour)));
    	source.sendFeedback(Text.literal("Time » " +  timeBlessing).styled(style -> style.withColor(colourProfile.infoColour)));
    	
    	source.sendFeedback(Text.literal("               ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));

        return Command.SINGLE_SUCCESS;
    }
    
    private static int printBlessings(FabricClientCommandSource source, String option) {
    	if("reset".equals(option)) {
    		Cache.resetBlessings();
			source.sendFeedback(Text.literal("Blessings » ").styled(style -> style.withColor(colourProfile.primaryColour))
					.append(Text.literal("Successfully reset the counter!").styled(style -> style.withColor(colourProfile.secondaryColour))));
			return Command.SINGLE_SUCCESS;
    	} else {
    		source.sendError(Text.literal("Invalid option!").styled(style -> style.withColor(Formatting.RED)));
    		return Command.SINGLE_SUCCESS;
    	}
    }
}

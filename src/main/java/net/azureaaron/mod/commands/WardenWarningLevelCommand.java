package net.azureaaron.mod.commands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Functions;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.Text;

public class WardenWarningLevelCommand {
	private static final Text DISCLAIMER = Text.literal("It isn't possible to find out the\nexact player who triggered the shrieker.\n\nThis may not be 100% accurate.").styled(style -> style.withColor(AaronModConfigManager.get().colourProfile.infoColour.getAsInt()));

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("wardenwarninglevel")
				.executes(context -> printWardenWarningLevel(context.getSource())));
	}
			
    private static int printWardenWarningLevel(FabricClientCommandSource source) {
    	ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
    	
    	int warningLevel = Cache.relativeWarningLevel();
    	int warningsLeft = (warningLevel == 0) ? 3 : 3 - warningLevel;
    	String lastTriggered = (Cache.lastShriekTime == 0) ? "Unknown" : Functions.toMostRelative(System.currentTimeMillis() - Cache.lastShriekTime);
    	String spacing = "                              ";
    	
        source.sendFeedback(Text.literal(spacing).styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
        
        source.sendFeedback(Text.literal("Warning Level » " + warningLevel).styled(style -> style.withColor(colourProfile.infoColour.getAsInt()).withHoverEvent(new HoverEvent(Action.SHOW_TEXT, DISCLAIMER))));
        source.sendFeedback(Text.literal(""));
        source.sendFeedback(Text.literal("Warnings Left » " + warningsLeft).styled(style -> style.withColor(colourProfile.infoColour.getAsInt())));
        source.sendFeedback(Text.literal("Last Triggered Shrieker » " + lastTriggered).styled(style -> style.withColor(colourProfile.infoColour.getAsInt())));
        
        source.sendFeedback(Text.literal(spacing).styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));

        return Command.SINGLE_SUCCESS;
    }
}

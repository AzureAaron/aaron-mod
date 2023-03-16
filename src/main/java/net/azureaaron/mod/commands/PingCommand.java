package net.azureaaron.mod.commands;

import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

public class PingCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("ping")
				.executes(context -> printPing(context.getSource())));
	}
		
    private static int printPing(FabricClientCommandSource source) {
        ClientPlayNetworkHandler networkHandler = source.getClient().getNetworkHandler();
        PlayerListEntry player = networkHandler.getPlayerListEntry(networkHandler.getProfile().getId());

        if (source.getClient().isInSingleplayer() || networkHandler == null || player == null) {
            source.sendFeedback(Text.literal("You're on a local server!").styled(style -> style.withColor(colourProfile.primaryColour)));
        } else {
            source.sendFeedback(Text.literal("Ping » ").styled(style -> style.withColor(colourProfile.primaryColour))
            		.append(Text.literal(String.valueOf(player.getLatency())).styled(style -> style.withColor(colourProfile.secondaryColour))));
        }

        return Command.SINGLE_SUCCESS;
    }
}

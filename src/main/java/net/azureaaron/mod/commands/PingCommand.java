package net.azureaaron.mod.commands;

import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.events.PingResultEvent;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class PingCommand {
	private static volatile boolean sentCommand = false;

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("ping")
				.executes(context -> handleCommand(context.getSource())));

		PingResultEvent.EVENT.register(PingCommand::onPingResult);
	}

	private static int handleCommand(FabricClientCommandSource source) {
		MinecraftClient client = source.getClient();
		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

		if (source.getClient().isInSingleplayer() || networkHandler == null) {
			source.sendFeedback(Text.literal("You're on a local server!").styled(style -> style.withColor(colourProfile.primaryColour)));
		} else {
			sendPingPacket(client, networkHandler);
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int printPing(MinecraftClient client, long ping) {
		client.player.sendMessage(Text.literal("Ping » ").styled(style -> style.withColor(colourProfile.primaryColour))
				.append(Text.literal(String.valueOf(ping) + " ms").styled(style -> style.withColor(colourProfile.secondaryColour))));

		return Command.SINGLE_SUCCESS;
	}

	private static void sendPingPacket(MinecraftClient client, ClientPlayNetworkHandler networkHandler) {
		sentCommand = true;
		if (!client.getDebugHud().shouldShowPacketSizeAndPingCharts()) networkHandler.sendPacket(new QueryPingC2SPacket(Util.getMeasuringTimeMs()));
	}

	private static void onPingResult(long ping) {
		if (sentCommand) {
			sentCommand = false;
			printPing(MinecraftClient.getInstance(), ping);
		}
	}
}

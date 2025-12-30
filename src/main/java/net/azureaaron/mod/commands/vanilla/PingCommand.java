package net.azureaaron.mod.commands.vanilla;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.events.PingResultCallback;
import net.azureaaron.mod.utils.Constants;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.util.Util;

public class PingCommand {
	private static volatile boolean sentCommand = false;

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(PingCommand::register);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("ping")
				.executes(context -> handleCommand(context.getSource())));

		PingResultCallback.EVENT.register(PingCommand::onPingResult);
	}

	private static int handleCommand(FabricClientCommandSource source) {
		ColourProfiles colourProfile = Constants.PROFILE.get();

		Minecraft client = source.getClient();
		ClientPacketListener networkHandler = client.getConnection();

		if (source.getClient().isLocalServer() || networkHandler == null) {
			source.sendFeedback(Constants.PREFIX.get().append(Component.literal("You're on a local server!").withColor(colourProfile.primaryColour.getAsInt())));
		} else {
			sendPingPacket(client, networkHandler);
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int printPing(Minecraft client, long ping) {
		ColourProfiles colourProfile = Constants.PROFILE.get();

		client.player.displayClientMessage(Component.literal("Ping » ").withColor(colourProfile.primaryColour.getAsInt())
				.append(Component.literal(ping + " ms").withColor(colourProfile.secondaryColour.getAsInt())), false);

		return Command.SINGLE_SUCCESS;
	}

	private static void sendPingPacket(Minecraft client, ClientPacketListener networkHandler) {
		sentCommand = true;
		if (!client.getDebugOverlay().showNetworkCharts()) networkHandler.send(new ServerboundPingRequestPacket(Util.getMillis()));
	}

	private static void onPingResult(long ping) {
		if (sentCommand) {
			sentCommand = false;
			printPing(Minecraft.getInstance(), ping);
		}
	}
}

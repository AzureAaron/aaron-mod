package net.azureaaron.mod.commands;

import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.annotations.VanillaBased;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.PerformanceLog;

public class PingCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("ping")
				.executes(context -> printPing(context.getSource())));
	}
		
    @SuppressWarnings("resource")
	private static int printPing(FabricClientCommandSource source) {
        ClientPlayNetworkHandler networkHandler = source.getClient().getNetworkHandler();
        PlayerListEntry player = networkHandler.getPlayerListEntry(networkHandler.getProfile().getId());

        if (source.getClient().isInSingleplayer() || networkHandler == null || player == null) {
            source.sendFeedback(Text.literal("You're on a local server!").styled(style -> style.withColor(colourProfile.primaryColour)));
        } else if (source.getClient().player.networkHandler.getServerInfo().isRealm() || Config.latencyFetchMode == Config.LatencyFetchMode.PLAYER_LIST) {
            source.sendFeedback(Text.literal("Ping » ").styled(style -> style.withColor(colourProfile.primaryColour))
            		.append(Text.literal(String.valueOf(player.getLatency()) + " ms").styled(style -> style.withColor(colourProfile.secondaryColour))));
        } else {
        	CompletableFuture.runAsync(() -> pingServer(source.getClient().player.networkHandler.getServerInfo(), source));
        }

        return Command.SINGLE_SUCCESS;
    }
    
    private static int printQueriedServerPing(FabricClientCommandSource source, PingResult result) {
    	if (result.result() == ActionResult.SUCCESS) {
            source.sendFeedback(Text.literal("Ping » ").styled(style -> style.withColor(colourProfile.primaryColour))
            		.append(Text.literal(String.valueOf(result.latency()) + " ms").styled(style -> style.withColor(colourProfile.secondaryColour))));
    	} else {
    		source.sendFeedback(Text.literal("Failed to get ping to the server!").styled(style -> style.withColor(colourProfile.primaryColour)));
    	}
    	
        return Command.SINGLE_SUCCESS;
    }
    
    @VanillaBased(MultiplayerServerListPinger.class)
	private static void pingServer(ServerInfo initialEntry, FabricClientCommandSource source) {
    	ServerInfo entry = new ServerInfo(initialEntry.name, initialEntry.address, ServerType.OTHER);
    	entry.copyWithSettingsFrom(initialEntry);
    	
    	ServerAddress address = ServerAddress.parse(entry.address);
    	Optional<InetSocketAddress> inetAddress = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);
    	
    	//From a blocked server
    	if (inetAddress.isEmpty()) {
    		printQueriedServerPing(source, new PingResult(0L, ActionResult.FAIL));
    		
    		return;
    	}
    	
    	InetSocketAddress inetSocketAddress = inetAddress.get();
    	ClientConnection clientConnection = ClientConnection.connect(inetSocketAddress, false, (PerformanceLog) null);
    	entry.ping = -1L;
    	
    	ClientQueryPacketListener clientQueryPacketListener = new ClientQueryPacketListener() {
    		private boolean received;
    		private long startTime;
    		
			@Override
			public void onResponse(QueryResponseS2CPacket packet) {
				if (this.received) {
					printQueriedServerPing(source, new PingResult(entry.ping, ActionResult.FAIL));
					return;
				}
				
				this.received = true;
				this.startTime = Util.getMeasuringTimeMs();
				clientConnection.send(new QueryPingC2SPacket(this.startTime));
			}
    		
			@Override
			public void onPingResult(PingResultS2CPacket packet) {
				long l = this.startTime;
				long m = Util.getMeasuringTimeMs();
				entry.ping = m - l;
				
				clientConnection.disconnect(null);
				printQueriedServerPing(source, new PingResult(entry.ping, ActionResult.SUCCESS));
			}

			@Override
			public void onDisconnected(Text text) {
				printQueriedServerPing(source, new PingResult(0L, ActionResult.FAIL));
			}

			@Override
			public boolean isConnectionOpen() {
				return clientConnection.isOpen();
			}
    	};
    	
    	try {
    		clientConnection.connect(address.getAddress(), address.getPort(), clientQueryPacketListener);
    		clientConnection.send(new QueryRequestC2SPacket());
    	} catch (Throwable t) {
    		t.printStackTrace();
    		printQueriedServerPing(source, new PingResult(0L, ActionResult.FAIL));
    		
    		if (clientConnection.isOpen()) clientConnection.disconnect(null);
    	}
    }
    
    private record PingResult(long latency, ActionResult result) {}
}

package net.azureaaron.mod.commands.vanilla;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.Set;
import java.util.function.Supplier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.events.PlaySoundEvent;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.Formatters;
import net.azureaaron.mod.utils.render.RenderHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class WardenWarningLevelCommand {
	private static final Supplier<Component> DISCLAIMER = () -> Component.literal("It isn't possible to find out the\nexact player who triggered the shrieker.\n\nThis may not be 100% accurate.").withStyle(style -> style.withColor(Constants.PROFILE.get().infoColour.getAsInt()));
	private static final Set<SoundEvent> WARNING_SOUNDS = Set.of(SoundEvents.WARDEN_NEARBY_CLOSE, SoundEvents.WARDEN_NEARBY_CLOSER, SoundEvents.WARDEN_NEARBY_CLOSEST, SoundEvents.WARDEN_LISTENING_ANGRY);

	//It isn't possible to find out which exact player trigger a shrieker so this may not be 100% accurate
	private static int warningLevel = 0;
	private static long lastShriekTime = 0L;

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(WardenWarningLevelCommand::register);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> reset());
		PlaySoundEvent.EVENT.register(WardenWarningLevelCommand::onPlaySound);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("wardenwarninglevel")
				.executes(context -> printWardenWarningLevel(context.getSource())));
	}

	private static int printWardenWarningLevel(FabricClientCommandSource source) {
		ColourProfiles colourProfile = Constants.PROFILE.get();

		int warningLevel = relativeWarningLevel();
		int warningsLeft = (warningLevel == 0) ? 3 : 3 - warningLevel;
		String lastTriggered = (lastShriekTime == 0) ? "Unknown" : Formatters.toRelativeTime(System.currentTimeMillis() - lastShriekTime).greatest();
		String spacing = "                              ";

		RenderHelper.runOnRenderThread(() -> {
			source.sendFeedback(Component.literal(spacing).withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));

			source.sendFeedback(Component.literal("Warning Level » " + warningLevel).withStyle(style -> style.withColor(colourProfile.infoColour.getAsInt()).withHoverEvent(new HoverEvent.ShowText(DISCLAIMER.get()))));
			source.sendFeedback(Component.literal(""));
			source.sendFeedback(Component.literal("Warnings Left » " + warningsLeft).withStyle(style -> style.withColor(colourProfile.infoColour.getAsInt())));
			source.sendFeedback(Component.literal("Last Triggered Shrieker » " + lastTriggered).withStyle(style -> style.withColor(colourProfile.infoColour.getAsInt())));

			source.sendFeedback(Component.literal(spacing).withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
		});

		return Command.SINGLE_SUCCESS;
	}

	private static void reset() {
		if (!Cache.lastServerAddress.equals(Cache.currentServerAddress)) {
			warningLevel = 0;
			lastShriekTime = 0L;
		}
	}

	private static void onPlaySound(ClientboundSoundPacket packet) {
		if (packet.getSource() == SoundSource.HOSTILE && WARNING_SOUNDS.contains(packet.getSound().value())) {
			lastShriekTime = System.currentTimeMillis();

			switch (packet.getSound().value()) {
				case SoundEvent s when s.equals(SoundEvents.WARDEN_NEARBY_CLOSE) -> warningLevel = 1;
				case SoundEvent s when s.equals(SoundEvents.WARDEN_NEARBY_CLOSER) -> warningLevel = 2;
				case SoundEvent s when s.equals(SoundEvents.WARDEN_NEARBY_CLOSEST) -> warningLevel = 3;
				case SoundEvent s when s.equals(SoundEvents.WARDEN_LISTENING_ANGRY) -> warningLevel = 4;

				default -> {}
			}
		}
	}

	private static int relativeWarningLevel() {
		int tenMinuteIncrementsPassed = (int) ((System.currentTimeMillis() - lastShriekTime) / 600_000L);

		return lastShriekTime == 0L ? 0 : Math.max(warningLevel - tenMinuteIncrementsPassed, 0);
	}
}

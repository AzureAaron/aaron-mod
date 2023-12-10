package net.azureaaron.mod.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.TeamUpdateEvent;
import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket.SerializableTeam;

public class TeamUpdateListener {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final Pattern SCORE_PATTERN = Pattern.compile("Cleared: [0-9]+% \\((?<score>[0-9]+)\\)");
	
	/**
	 * This method returns the dungeon score for a run, this is done by adding {@code 28} 
	 * to the score reported in the scoreboard which is accurate until players have entered the boss room.<br><br>
	 * 
	 * We also don't need to check if Mayor Paul's Marauder perk is active since the +10 score is automatically apart of {@code scoreboardScore}.
	 * 
	 * @param scoreboardScore The dungeon score from the scoreboard
	 * @return The current dungeon score
	 */
	private static int fastApproxScore(int scoreboardScore) {
		return Cache.inDungeonBossRoom ? scoreboardScore : scoreboardScore + 28;
	}

	public static void listen() {
		TeamUpdateEvent.EVENT.register((packet) -> {
			if (Functions.isOnHypixel() && packet.getTeam().isPresent()) {
				SerializableTeam team = packet.getTeam().get();
				String display = (team.getPrefix().getString() + team.getSuffix().getString()).trim();
				Matcher scoreMatcher = SCORE_PATTERN.matcher(display);
								
				if (scoreMatcher.matches() && AaronModConfigManager.get().dungeonScoreMessage) {
					int score = fastApproxScore(Integer.parseInt(scoreMatcher.group("score")));
					Cache.currentScore = score;
					
					if (score >= 270 && score < 300 && Cache.lastTwoHundredSeventyScore == 0L) {
						Cache.lastTwoHundredSeventyScore = System.currentTimeMillis();
						String S270 = AaronModConfigManager.get().twoHundredSeventyScore.trim();
						CLIENT.player.networkHandler.sendChatMessage("270 Score → " + S270.substring(0, Math.min(S270.length(), 244)));
					}
					
					if (score >= 300 && Cache.lastThreeHundredScore == 0L) {
						Cache.lastThreeHundredScore = System.currentTimeMillis();
						String S300 = AaronModConfigManager.get().threeHundredScore.trim();
						CLIENT.player.networkHandler.sendChatMessage("300 Score → " + S300.substring(0, Math.min(S300.length(), 244)));
					};
				};
			}
		});
	}
}

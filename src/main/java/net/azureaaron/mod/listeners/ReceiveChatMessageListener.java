package net.azureaaron.mod.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.events.ReceiveChatMessageEvent;
import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

public class ReceiveChatMessageListener {
	private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
	private static final Pattern PARTY_PATTERN = Pattern.compile("Party Finder > (?<name>[A-z0-9_]+) joined the dungeon group! \\([^)]*\\)");
	private static final Pattern PLAYER_BLESSING_PATTERN = Pattern.compile("DUNGEON BUFF! [A-z0-9_]+ found a Blessing of (?<blessing>[A-z]+) (?<level>[A-z]+)!( \\([^)]*\\))?");
	private static final Pattern AUTO_PICKUP_BLESSING_PATTERN = Pattern.compile("DUNGEON BUFF! A Blessing of (?<blessing>[A-z]+) (?<level>[A-z]+) was found!( \\([^)]*\\))?");
	private static final Pattern TEAM_SCORE_PATTERN = Pattern.compile(" +Team Score: [0-9]+ \\([A-z+]+\\)");
	
	public static void listen() {
		ReceiveChatMessageEvent.EVENT.register((message, stringForm) -> {	
			if(Functions.isOnHypixel()) {
				//String lowerForm = stringForm.toLowerCase();
				String strippedForm = Formatting.strip(stringForm);
				Matcher partyMatcher = PARTY_PATTERN.matcher(stringForm);
				Matcher playerBlessingMatcher = PLAYER_BLESSING_PATTERN.matcher(stringForm);
				Matcher autoBlessingMatcher = AUTO_PICKUP_BLESSING_PATTERN.matcher(stringForm);
				Matcher teamScoreMatcher = TEAM_SCORE_PATTERN.matcher(stringForm);
				
				if(Config.dungeonFinderPersonStats && partyMatcher.matches()) minecraftClient.player.networkHandler.sendCommand("dungeons " + partyMatcher.group("name"));
				
				if(playerBlessingMatcher.matches()) Cache.incrementBlessing(playerBlessingMatcher.group("blessing"), playerBlessingMatcher.group("level"));

				if(autoBlessingMatcher.matches()) Cache.incrementBlessing(autoBlessingMatcher.group("blessing"), autoBlessingMatcher.group("level"));
				
				//TODO Implement mort thingy for more acccurate resetting
				if(teamScoreMatcher.matches() || strippedForm.equals("[NPC] Mort: Here, I found this map when I first entered the dungeon.")) {
					Cache.resetBlessings();	
					Cache.lastTwoHundredSeventyScore = 0L;
					Cache.lastThreeHundredScore = 0L;
					Cache.inM7Phase5 = false;
					Cache.inDungeonBossRoom = false;
					Cache.currentScore = 0;
					
					Cache.powerDragonId = 0;
					Cache.flameDragonId = 0;
					Cache.apexDragonId = 0;
					Cache.iceDragonId = 0;
					Cache.soulDragonId = 0;
					
					Cache.powerSpawnStart = 0L;
					Cache.flameSpawnStart = 0L;
					Cache.apexSpawnStart = 0L;
					Cache.iceSpawnStart = 0L;
					Cache.soulSpawnStart = 0L;
				}
				
				if(stringForm.equals("[BOSS] Bonzo: Gratz for making it this far, but I'm basically unbeatable.") || stringForm.equals("[BOSS] Scarf: This is where the journey ends for you, Adventurers.")
						|| stringForm.equals("[BOSS] The Professor: I was burdened with terrible news recently...") || stringForm.equals("[BOSS] Thorn: Welcome Adventurers! I am Thorn, the Spirit! And host of the Vegan Trials!")
						|| stringForm.equals("[BOSS] Livid: Welcome, you arrive right on time. I am Livid, the Master of Shadows.") || stringForm.equals("[BOSS] Sadan: So you made it all the way here... Now you wish to defy me? Sadan?!")
						|| stringForm.equals("[BOSS] Maxor: WELL WELL WELL LOOK WHO'S HERE!")) Cache.inDungeonBossRoom = true;

				/*if(Config.dungeonScoreMessage && ((lowerForm.contains("skytils-sc >") && lowerForm.contains("270") && !lowerForm.contains("300")) || lowerForm.contains("270 score reached!")) && Cache.lastTwoHundredSeventyScore + 20000L < System.currentTimeMillis()) {
					Cache.lastTwoHundredSeventyScore = System.currentTimeMillis();
					String S270 = Config.twoHundredSeventyScore.trim();
					minecraftClient.player.networkHandler.sendChatMessage("270 Score → " + S270.substring(0, Math.min(S270.length(), 244)));
				}
				
				if(Config.dungeonScoreMessage && ((lowerForm.contains("skytils-sc >") && lowerForm.contains("300") && !lowerForm.contains("270")) || lowerForm.contains("300 score reached!")) && Cache.lastThreeHundredScore + 20000L < System.currentTimeMillis()) {
					Cache.lastThreeHundredScore = System.currentTimeMillis();
					String S300 = Config.threeHundredScore.trim();
					minecraftClient.player.networkHandler.sendChatMessage("300 Score → " + S300.substring(0, Math.min(S300.length(), 244)));
				}*/
				
				if(stringForm.equals("[BOSS] Wither King: You.. again?") || stringForm.equals("[BOSS] Wither King: Ohhh?")) Cache.inM7Phase5 = true;
			}
			return ActionResult.SUCCESS;
		});
	}
}

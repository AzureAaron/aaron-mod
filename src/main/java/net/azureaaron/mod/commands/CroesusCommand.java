package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.Messages;
import net.azureaaron.mod.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.util.Session;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.HoverEvent.ItemStackContent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CroesusCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("croesus")
				.executes(context -> handleCommand(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "player")))));
	}
	
	private static final String[] RARE_LOOT = {/*M7*/ "dark_claymore", "necron_handle", "wither_shield_scroll",
			"implosion_scroll", "shadow_warp_scroll", "fifth_master_star", "necron_dye", "thunderlord_7", "master_skull_tier_5",
			/*M6*/ "giants_sword", "fourth_master_star", /*M5*/ "shadow_fury", "shadow_assassin_chestplate", "third_master_star", 
			/*M4*/ "spirit_wing", "item_spirit_bow", "second_master_star", /*M3*/ "first_master_star", /*All Floors*/ "recombobulator_3000"};
	
	private static final Text NO_TREASURES = Text.literal("This player doesn't have any dungeon treasures to claim!").styled(style -> style.withColor(Formatting.RED));
	
	private static int handleCommand(FabricClientCommandSource source) {
		if(StringUtils.isBlank(Config.key)) {
			source.sendFeedback(Messages.NO_API_KEY_ERROR);
			return Command.SINGLE_SUCCESS;
		}
		
		Session session = source.getClient().getSession();
						
		CompletableFuture.supplyAsync(() -> {
			try {
				return Http.sendHypixelRequest("skyblock/profiles", "&uuid=" + session.getUuid(), true, false);
			} catch (Exception e) {
				source.sendError(Messages.SKYBLOCK_PROFILES_FETCH_ERROR);
				e.printStackTrace();
			}
			return null;
		})
		.thenApply(body -> {
			try {
				return Skyblock.getSelectedProfile2(body);
			} catch (Exception e) {
				source.sendError(Messages.JSON_PARSING_ERROR);
				e.printStackTrace();
			}
			return null;
		}).thenAccept(body -> printCroesus(body, source, session.getUuid(), session.getUsername()));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static volatile String name = null;
	private static volatile String uuid = null;
	private static volatile boolean shouldSkip = false;
	
	private static int handleCommand(FabricClientCommandSource source, String player) {
		if(StringUtils.isBlank(Config.key)) {
			source.sendFeedback(Messages.NO_API_KEY_ERROR);
			return Command.SINGLE_SUCCESS;
		}
		
		CompletableFuture.supplyAsync(() -> {
			try {
				String response = Http.sendNameToUuidRequest(player);
				JsonObject json = JsonParser.parseString(response).getAsJsonObject();
				name = json.get("name").getAsString();
				uuid = json.get("id").getAsString();
			} catch (Exception e) {
				source.sendError(Messages.NAME_TO_UUID_ERROR);
				shouldSkip = true;
				e.printStackTrace();
			}
			return null;
		})
		.thenApply(x -> {
			try {
				return Http.sendHypixelRequest("skyblock/profiles", "&uuid=" + uuid, true, shouldSkip);
			} catch (Exception e) {
				source.sendError(Messages.SKYBLOCK_PROFILES_FETCH_ERROR);
				e.printStackTrace();
			}
			return null;
		})
		.thenApply(body -> {
			try {
				return Skyblock.getSelectedProfile2(body);
			} catch (Exception e) {
				source.sendError(Messages.JSON_PARSING_ERROR);
				e.printStackTrace();
			}
			return null;
		}).thenAccept(body -> printCroesus(body, source, uuid, name));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private record ChestData(String runId, String type, JsonArray rewards) {}
	private record RunData(long timestamp, int floor, String dungeon, List<ChestData> chests) {}
	
	private static void printCroesus(JsonObject body, FabricClientCommandSource source, String uuid, String name) {
		CroesusCommand.name = null;
		CroesusCommand.uuid = null;
		shouldSkip = false;
				
		if(body == null) return;
		
		JsonObject profile = body.get("members").getAsJsonObject().get(uuid).getAsJsonObject();
		String endSpaces = "        " + name.replaceAll("[A-z0-9_]", "  ") + "        ";
		
		//The Croesus api is a complete NIGHTMARE! you have been warned!!
		
		if(profile.get("dungeons").getAsJsonObject().get("treasures").getAsJsonObject().get("runs") == null) {
			source.sendError(NO_TREASURES);
			return;
		}
		
		long twoDaysAgo = (long) (System.currentTimeMillis() - 1.728e+8);
		HashMap<String, RunData> runs = new HashMap<String, RunData>(); //HashSet containing all eligible runs for this command and various data about them from the api
		HashSet<String> ineligibleRuns = new HashSet<String>(); //HashSet containing the ids of runs ineligible to be displayed/used by this command
		
		//Iterate over all runs available in the api and if they weren't done over 2 days ago and weren't from entrance add them to the runs HashSet
		for(JsonElement run : profile.get("dungeons").getAsJsonObject().get("treasures").getAsJsonObject().get("runs").getAsJsonArray()) {
			JsonObject iteratedRun = run.getAsJsonObject();
			if(iteratedRun.get("completion_ts").getAsLong() > twoDaysAgo && iteratedRun.get("dungeon_tier").getAsInt() != 0) {
				runs.put(iteratedRun.get("run_id").getAsString(), new RunData(iteratedRun.get("completion_ts").getAsLong(), iteratedRun.get("dungeon_tier").getAsInt(), iteratedRun.get("dungeon_type").getAsString(), new ArrayList<ChestData>()));
			} else {
				ineligibleRuns.add(iteratedRun.get("run_id").getAsString());
			}
		}
		
		if(runs.size() == 0) {
			source.sendError(NO_TREASURES);
			return;
		}

		HashSet<String> claimedRuns = new HashSet<String>(); //HashSet containing all the ids of runs where chests were paid for
		HashSet<ChestData> chests = new HashSet<ChestData>(); //HashSet containing all unpaid chests
		List<String> rewards = new ArrayList<String>(); //HashSet containing all of the rewards earned across all unopened chests

		//Iterate over all chests available to determine if they were or weren't claimed then perform logic (read other comments)
		for(JsonElement chest : profile.get("dungeons").getAsJsonObject().get("treasures").getAsJsonObject().get("chests").getAsJsonArray()) {
			JsonObject iteratedChest = chest.getAsJsonObject();
			String chestRunId = iteratedChest.get("run_id").getAsString();
			boolean chestPaid = iteratedChest.get("paid").getAsBoolean();
			
			//If the chest isn't yet apart of the claimedRuns HashSet and it was paid for
			if(!claimedRuns.contains(chestRunId) && chestPaid) {
				claimedRuns.add(chestRunId);
				runs.remove(chestRunId);
				continue;
			}
			
			//If the chest hasn't been paid for yet
			if(!chestPaid) chests.add(new ChestData(chestRunId, iteratedChest.get("treasure_type").getAsString(), iteratedChest.get("rewards").getAsJsonObject().get("rewards").getAsJsonArray()));
		}
				
		//Append chests to the run they're associated with.
		chests.forEach(chest -> {
			if(!claimedRuns.contains(chest.runId()) && !ineligibleRuns.contains(chest.runId())) { //If the chest hasn't been claimed and isn't from an ineligible run
				RunData old = runs.get(chest.runId());
				runs.put(chest.runId(), new RunData(old.timestamp(), old.floor(), old.dungeon(), Stream.concat(old.chests().stream(), Arrays.asList(chest).stream()).toList()));
				
				//Add rewards to the rewards HashSet to later check for rare drops 
				//rare drops can be duplicated in the list however common drops are deduplicated.
				chest.rewards().forEach((element) -> {
					String stringForm = element.getAsString();
					boolean isRareLoot = Arrays.stream(RARE_LOOT).anyMatch(stringForm::equals);
					if(isRareLoot || (!isRareLoot && !rewards.contains(stringForm))) rewards.add(stringForm);
				});
			}
		});
		
		if(runs.size() == 0) {
			source.sendError(NO_TREASURES);
			return;
		}
		
		String rewardsString = rewards.toString();
		boolean rareLootAwaits = Arrays.stream(RARE_LOOT).anyMatch(rewardsString::contains);
		Function<String, Boolean> containsRareLoot = s -> Arrays.stream(RARE_LOOT).anyMatch(s::equals);
		String[] rareLoot = rewards.stream().filter(e -> containsRareLoot.apply(e)).toArray(String[]::new);
		
		ItemStack bundle = Items.BUNDLE.getDefaultStack().setCustomName(Text.literal("✦ Rare Loot Preview ✦").styled(style -> style.withItalic(false).withColor(colourProfile.infoColour)));
		
		for(int i = 0; i < rareLoot.length; i++) {
			Functions.addToBundle(bundle, Skyblock.RARE_LOOT_ITEMS.get(rareLoot[i]));
		}
				
		source.sendFeedback(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true)))));
		
		source.sendFeedback(Text.literal("Unclaimed Chests » " + runs.size()).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Rare Loot Awaits » " + ((rareLootAwaits) ? "✓" : "✗"))
				.styled(style -> style.withColor(colourProfile.infoColour).withHoverEvent(new HoverEvent(Action.SHOW_ITEM, new ItemStackContent(bundle)))));
		source.sendFeedback(Text.literal(""));
		
		int count = 0;
		for(RunData run : runs.values()) {
			if(count <= 10) {
				String floorShorthand = String.valueOf(Character.toUpperCase(run.dungeon().charAt(0))).replace('C', 'F') + run.floor();
				String timeAgo = Functions.toRelative(System.currentTimeMillis() - run.timestamp()).split(",")[0].replaceAll(" ago", "") + " ago";
				long expiresAt = (long) (run.timestamp() + 1.728e+8);
				long expiresIn = (long) (run.timestamp() + 1.728e+8) - System.currentTimeMillis();
				
				source.sendFeedback(Text.literal("(" + floorShorthand + " • " + timeAgo + ")")
						.styled(style -> style.withColor(colourProfile.hoverColour)
								.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Expires:\n" + Functions.DATE_FORMATTER.format(Instant.ofEpochMilli(expiresAt)) + "\n(" + TimeUnit.MILLISECONDS.toHours(expiresIn) + " hours)")
										.styled(style1 -> style1.withColor(colourProfile.infoColour))))));
				count++;
			}
		}
		
		if(count > 10) source.sendFeedback(Text.literal("and " + (runs.size()-10) + " more...").styled(style -> style.withColor(colourProfile.supportingInfoColour).withItalic(true)));
		
		source.sendFeedback(Text.literal(endSpaces).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
		
		return;
	}
}

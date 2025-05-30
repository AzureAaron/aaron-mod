package net.azureaaron.mod.commands.skyblock;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.commands.Command;
import net.azureaaron.mod.commands.CommandSystem;
import net.azureaaron.mod.commands.SkyblockCommand;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.Formatters;
import net.azureaaron.mod.utils.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class CroesusCommand extends SkyblockCommand {
	private static final Command INSTANCE = new CroesusCommand();
	private static final long TWO_DAYS = 172_800_000;
	private static final Supplier<MutableText> NO_TREASURES = () -> Constants.PREFIX.get().append(Text.literal("This player doesn't have any dungeon treasures to claim!").formatted(Formatting.RED));

	@Init
	public static void init() {
		if (AaronModConfigManager.get().skyblock.commands.enableSkyblockCommands) ClientCommandRegistrationCallback.EVENT.register(INSTANCE::register);
	}

	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(literal("croesus")
				.executes(context -> CommandSystem.handleSelf4Skyblock(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(this, context.getSource(), getString(context, "player")))));
	}

	@Override
	public void print(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		ColourProfiles colourProfile = Constants.PROFILE.get();
		
		JsonObject profile = body.getAsJsonObject("members").getAsJsonObject(uuid);
		
		//The Croesus api is a complete NIGHTMARE! you have been warned!!
		
		JsonObject treasures = profile.getAsJsonObject("dungeons").getAsJsonObject("treasures");
		
		if (treasures.get("runs") == null) {
			source.sendError(NO_TREASURES.get());
			
			return;
		}
		
		long twoDaysAgo = System.currentTimeMillis() - TWO_DAYS;
		HashMap<String, RunData> runs = new HashMap<String, RunData>(); //HashSet containing all eligible runs for this command and various data about them from the api
		HashSet<String> ineligibleRuns = new HashSet<String>(); //HashSet containing the ids of runs ineligible to be displayed/used by this command
		
		//Iterate over all runs available in the api and if they weren't done over 2 days ago and weren't from entrance add them to the runs HashSet
		for (JsonElement run : treasures.get("runs").getAsJsonArray()) {
			JsonObject iteratedRun = run.getAsJsonObject();
			
			if (iteratedRun.get("completion_ts").getAsLong() > twoDaysAgo && iteratedRun.get("dungeon_tier").getAsInt() != 0) {
				runs.put(iteratedRun.get("run_id").getAsString(), new RunData(iteratedRun.get("completion_ts").getAsLong(), iteratedRun.get("dungeon_tier").getAsInt(), iteratedRun.get("dungeon_type").getAsString(), new ArrayList<ChestData>()));
			} else {
				ineligibleRuns.add(iteratedRun.get("run_id").getAsString());
			}
		}
		
		if (runs.size() == 0) {
			source.sendError(NO_TREASURES.get());
			return;
		}

		HashSet<String> claimedRuns = new HashSet<String>(); //HashSet containing all the ids of runs where chests were paid for
		HashSet<ChestData> chests = new HashSet<ChestData>(); //HashSet containing all unpaid chests
		List<String> rewards = new ArrayList<String>(); //HashSet containing all of the rewards earned across all unopened chests

		//Iterate over all chests available to determine if they were or weren't claimed then perform logic (read other comments)
		for (JsonElement chest : treasures.get("chests").getAsJsonArray()) {
			JsonObject iteratedChest = chest.getAsJsonObject();
			String chestRunId = iteratedChest.get("run_id").getAsString();
			boolean chestPaid = iteratedChest.get("paid").getAsBoolean();
			
			//If the chest isn't yet apart of the claimedRuns HashSet and it was paid for
			if (!claimedRuns.contains(chestRunId) && chestPaid) {
				claimedRuns.add(chestRunId);
				runs.remove(chestRunId);
				
				continue;
			}
			
			//If the chest hasn't been paid for yet
			if (!chestPaid) chests.add(new ChestData(chestRunId, iteratedChest.get("treasure_type").getAsString(), iteratedChest.get("rewards").getAsJsonObject().get("rewards").getAsJsonArray()));
		}
				
		//Append chests to the run they're associated with.
		chests.forEach(chest -> {
			if (!claimedRuns.contains(chest.runId()) && !ineligibleRuns.contains(chest.runId())) { //If the chest hasn't been claimed and isn't from an ineligible run
				RunData old = runs.get(chest.runId());
				runs.put(chest.runId(), new RunData(old.timestamp(), old.floor(), old.dungeon(), Stream.concat(old.chests().stream(), Arrays.asList(chest).stream()).toList()));
				
				//Add rewards to the rewards HashSet to later check for rare drops 
				//rare drops can be duplicated in the list however common drops are deduplicated.
				chest.rewards().forEach((element) -> {
					String stringForm = element.getAsString();
					boolean isRareLoot = Skyblock.getRareLootItems().keySet().stream().anyMatch(stringForm::equals);
					
					if (isRareLoot || (!isRareLoot && !rewards.contains(stringForm))) rewards.add(stringForm);
				});
			}
		});
		
		if (runs.size() == 0) {
			source.sendError(NO_TREASURES.get());
			
			return;
		}
		
		String rewardsString = rewards.toString();
		boolean rareLootAwaits = Skyblock.getRareLootItems().keySet().stream().anyMatch(rewardsString::contains);
		Function<String, Boolean> containsRareLoot = s -> Skyblock.getRareLootItems().keySet().stream().anyMatch(s::equals);
		List<String> rareLoot = rewards.stream().filter(e -> containsRareLoot.apply(e)).collect(Collectors.toList());
		
		ItemStack bundle = Items.BUNDLE.getDefaultStack();
		bundle.set(DataComponentTypes.CUSTOM_NAME, Text.literal("✦ Rare Loot Preview ✦").styled(style -> style.withItalic(false).withColor(colourProfile.infoColour.getAsInt())));
		
		//We use a map because ItemStacks have no determinate hash code thus we need a key that to allow for deduplication which will result in the same rare drops being stacked
		HashMap<String, ItemStack> stacks = new HashMap<>();

		for (String item : rareLoot) {
			int occurrences = Collections.frequency(rareLoot, item);

			//We need to copy the item stack because you can't share the same stack across different bundles for some reason?
			if (!stacks.containsKey(item)) {
				stacks.put(item, Util.make(Skyblock.getRareLootItems().get(item).copy(), stack -> stack.setCount(occurrences)));
			}
		}

		bundle.set(DataComponentTypes.BUNDLE_CONTENTS, new BundleContentsComponent(new ArrayList<>(stacks.values())));
				
		Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt())).styled(style -> style.withStrikethrough(true))));
		
		source.sendFeedback(startText);
		
		source.sendFeedback(Text.literal("Unclaimed Chests » " + runs.size()).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Rare Loot Awaits » " + ((rareLootAwaits) ? "✓" : "✗"))
				.styled(style -> style.withColor(colourProfile.infoColour.getAsInt()).withHoverEvent(new HoverEvent.ShowItem(bundle))));
		source.sendFeedback(Text.literal(""));
		
		int count = 0;
		
		//TODO Refactor with regular loop using Math.min(runs.size(), 10)
		for (RunData run : runs.values()) {
			if (count <= 10) {
				String floorShorthand = String.valueOf(Character.toUpperCase(run.dungeon().charAt(0))).replace('C', 'F') + run.floor();
				String timeAgo = Formatters.toRelativeTime(System.currentTimeMillis() - run.timestamp()).atMost(2);
				long expiresAt = run.timestamp() + TWO_DAYS;
				long expiresIn = (run.timestamp() + TWO_DAYS) - System.currentTimeMillis();
				
				source.sendFeedback(Text.literal("(" + floorShorthand + " • " + timeAgo + ")")
						.styled(style -> style.withColor(colourProfile.hoverColour.getAsInt())
								.withHoverEvent(new HoverEvent.ShowText(Text.literal("Expires:\n" + Formatters.DATE_FORMATTER.format(Instant.ofEpochMilli(expiresAt)) + "\n(In " + TimeUnit.MILLISECONDS.toHours(expiresIn) + " hours)")
										.withColor(colourProfile.infoColour.getAsInt())))));
				count++;
			}
		}
		
		if (count > 10) source.sendFeedback(Text.literal("and " + (runs.size() - 10) + " more...").styled(style -> style.withColor(colourProfile.supportingInfoColour.getAsInt()).withItalic(true)));
		
		source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
	}

	private record ChestData(String runId, String type, JsonArray rewards) {}
	private record RunData(long timestamp, int floor, String dungeon, List<ChestData> chests) {}
}

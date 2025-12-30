package net.azureaaron.mod.commands.skyblock;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.io.IOException;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.commands.Command;
import net.azureaaron.mod.commands.CommandSystem;
import net.azureaaron.mod.commands.SkyblockCommand;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.skyblock.item.Accessories;
import net.azureaaron.mod.skyblock.item.Accessory;
import net.azureaaron.mod.skyblock.item.MagicalPower;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.Formatters;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.ItemUtils;
import net.azureaaron.mod.utils.JsonHelper;
import net.azureaaron.mod.utils.Messages;
import net.azureaaron.mod.utils.Skyblock;
import net.azureaaron.mod.utils.render.RenderHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;

public class MagicalPowerCommand extends SkyblockCommand {
	private static final Command INSTANCE = new MagicalPowerCommand();
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Supplier<MutableComponent> NO_ACCESSORY_BAG_DATA = () -> Constants.PREFIX.get().append(Component.literal("This profile doesn't have any accessory bag data!").withStyle(ChatFormatting.RED));
	private static final Supplier<MutableComponent> NBT_PARSING_ERROR = () -> Constants.PREFIX.get().append(Component.literal("There was an error while trying to parse NBT!").withStyle(ChatFormatting.RED)); //TODO make constant
	private static final Pattern ACCESSORY_RARITY_PATTERN = Pattern.compile("(?:a )?(?<rarity>(?:VERY )?[A-Za-z]+) (?:DUNGEON )?(?:AC|HAT)CESSORY(?: a)?");
	private static final IntToDoubleFunction STATS_MULT = magicalPower -> 29.97d * Math.pow(Math.log(Math.fma(0.0019d, magicalPower, 1d)), 1.2d);
	private static final Object2IntOpenHashMap<String> RARITY_TIER_MAP = Util.make(new Object2IntOpenHashMap<>(), map -> {
		map.put("VERY SPECIAL", 8);
		map.put("SPECIAL", 7);
		map.put("MYTHIC", 6);
		map.put("LEGENDARY", 5);
		map.put("EPIC", 4);
		map.put("RARE", 3);
		map.put("UNCOMMON", 2);
		map.put("COMMON", 1);
	});

	@Init
	public static void init() {
		if (AaronModConfigManager.get().skyblock.commands.enableSkyblockCommands) ClientCommandRegistrationCallback.EVENT.register(INSTANCE::register);
	}

	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("magicalpower")
				.executes(context -> CommandSystem.handleSelf4Skyblock(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> SharedSuggestionProvider.suggest(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(this, context.getSource(), getString(context, "player")))));

		dispatcher.register(literal("mp")
				.executes(context -> CommandSystem.handleSelf4Skyblock(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> SharedSuggestionProvider.suggest(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(this, context.getSource(), getString(context, "player")))));
	}

	@Override
	public void print(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		ColourProfiles colourProfile = Constants.PROFILE.get();

		JsonObject profile = body.getAsJsonObject("members").getAsJsonObject(uuid);

		JsonObject inventoryData = profile.getAsJsonObject("inventory");
		boolean inventoryEnabled = Skyblock.isInventoryApiEnabled(inventoryData);

		if (!inventoryEnabled) {
			source.sendError(Messages.INVENTORY_API_DISABLED_ERROR.get());

			return;
		}

		JsonObject accessoryBagStorage = profile.getAsJsonObject("accessory_bag_storage");

		if (accessoryBagStorage == null) {
			source.sendError(NO_ACCESSORY_BAG_DATA.get());

			return;
		}

		List<ItemStack> accessories;

		try {
			accessories = ItemUtils.parseCompressedItemData(JsonHelper.getString(inventoryData, "bag_contents.talisman_bag.data").orElseThrow());
		} catch (IOException e) {
			source.sendError(NBT_PARSING_ERROR.get());
			LOGGER.error("[Aaron's Mod] Encountered an exception while parsing NBT!", e);

			return;
		}

		//This can contain duplicates, we will deduplicate after collection
		ObjectArrayList<Pair<Accessory, String>> collectedAccessoriesFromBag = new ObjectArrayList<>();

		//Loop through the accessories
		for (ItemStack stack : accessories) {
			if (!stack.has(DataComponents.LORE)) continue; //Item is probably not an accessory

			String itemId = ItemUtils.getId(stack);

			//Determine the rarity - iterate backwards for efficiency
			for (Component line : stack.get(DataComponents.LORE).lines().reversed()) {
				String loreLine = ChatFormatting.stripFormatting(line.getString()); //Strip formatting again just in case it fails in original parsing
				Matcher matcher = ACCESSORY_RARITY_PATTERN.matcher(loreLine);

				if (!itemId.isBlank() && matcher.matches()) {
					collectedAccessoriesFromBag.add(ObjectObjectImmutablePair.of(Accessories.getAccessories().getOrDefault(itemId, Accessory.fromId(itemId)), matcher.group("rarity")));

					break;
				}
			}
		}

		Object2ObjectOpenHashMap<String, Pair<Accessory, String>> collectedAccessories = collectedAccessoriesFromBag.stream()
				.filter(pair -> {
					Accessory accessory = pair.left();

					int highestTierOfSameCollected = collectedAccessoriesFromBag.stream()
							.filter(c -> c.left().id().equals(accessory.id()))
							.mapToInt(c -> RARITY_TIER_MAP.getOrDefault(c.right(), 0))
							.max().orElse(0);

					//Drop if there is a higher rarity of the same accessory collected
					return highestTierOfSameCollected != 0 ? !(highestTierOfSameCollected > RARITY_TIER_MAP.getOrDefault(pair.right(), 0)) : true;
				})
				.collect(Collectors.toMap(p -> p.left().id(), Function.identity(), (oldValue, newValue) -> newValue, Object2ObjectOpenHashMap::new));

		int magicalPower = 0;
		int hegeMp = 0;
		int abicaseMp = 0;

		@SuppressWarnings("unused")
		int verySpecials = 0, specials = 0, mythics = 0, legendaries = 0, epics = 0, rares = 0, uncommons = 0, commons = 0;

		//Remove accessories which have higher-tiered siblings in the same family
		Object2ObjectOpenHashMap<String, Pair<Accessory, String>> validAccessories = collectedAccessories.object2ObjectEntrySet().stream()
				.map(Entry::getValue)
				.filter(a -> {
					Accessory accessory = a.left();

					boolean hasGreaterTierOfSameFamily = collectedAccessories.object2ObjectEntrySet().stream()
							.map(Entry::getValue)
							.map(Pair::left)
							.filter(ca -> ca.family().isPresent()) //Filter out accessories with no family - if we don't then if the accessory itself has no family then well...
							.filter(accessory::hasSameFamily) //If the accessories are apart of the same family
							.filter(ca -> ca.tier() > accessory.tier()) //If the checked accessory's tier is higher then the one from the main set
							.findAny().isPresent();

					//Drop if there is an accessory in the set in the same family with a higher tier
					return !hasGreaterTierOfSameFamily;
				})
				.filter(a -> {
					Accessory accessory = a.left();

					IntSummaryStatistics accessoryTierSummary = Accessories.getAccessories().entrySet().stream()
							.map(Entry::getValue)
							.filter(ca -> ca.family().isPresent()) //Filter out accessories with no family - if we don't then if the accessory itself has no family then well...
							.filter(accessory::hasSameFamily) //If the accessories are apart of the same family
							.mapToInt(Accessory::tier)
							.summaryStatistics();

					boolean allAccessoriesFromFamilyAreTheSameTier = accessoryTierSummary.getMin() == accessory.tier() && accessoryTierSummary.getMax() == accessory.tier();

					int greatestHashCodeInFamily = collectedAccessories.object2ObjectEntrySet().stream()
							.map(Entry::getValue)
							.map(Pair::left)
							.filter(ca -> ca.family().isPresent()) //Filter out accessories with no family - if we don't then if the accessory itself has no family then well...
							.filter(accessory::hasSameFamily) //If the accessories are apart of the same family
							.mapToInt(Accessory::hashCode)
							.max().orElse(0); //Find the highest hash code of all accessories and default to the null hash code

					//Drop if all the accessories in the family have the same tier and this accessory isn't the one with the greatest hash code
					return allAccessoriesFromFamilyAreTheSameTier ? accessory.hashCode() == greatestHashCodeInFamily : true;
				})
				.collect(Collectors.toMap(p -> p.left().id(), Function.identity(), (oldValue, newValue) -> newValue, Object2ObjectOpenHashMap::new));

		//Calculate the magical power!
		for (Map.Entry<String, Pair<Accessory, String>> accessory : validAccessories.entrySet()) {
			String id = accessory.getKey();
			String rarity = accessory.getValue().right();

			int mpToAdd = 0;

			switch (rarity) {
				case "VERY SPECIAL" -> {
					mpToAdd = 5;
					verySpecials++;
				}
				case "SPECIAL" -> {
					mpToAdd = 3;
					specials++;
				}
				case "MYTHIC" -> {
					mpToAdd = 22;
					mythics++;
				}
				case "LEGENDARY" -> {
					mpToAdd = 16;
					legendaries++;
				}
				case "EPIC" -> {
					mpToAdd = 12;
					epics++;
				}
				case "RARE" -> {
					mpToAdd = 8;
					rares++;
				}
				case "UNCOMMON" -> {
					mpToAdd = 5;
					uncommons++;
				}
				case "COMMON" -> {
					mpToAdd = 3;
					commons++;
				}

				default -> LOGGER.warn("[Aaron's Mod] Unrecognized accessory rarity \"{}\", please report this!", rarity);
			}

			//Calculate mp to earn from hege
			if (id.equals("HEGEMONY_ARTIFACT")) hegeMp = mpToAdd;

			//Calculate mp to earn from the abicase bonus
			if (id.equals("ABICASE")) {
				JsonObject crimsonIsleData = profile.getAsJsonObject("nether_island_player_data");

				if (crimsonIsleData != null && crimsonIsleData.has("abiphone")) {
					JsonObject abiphone = crimsonIsleData.getAsJsonObject("abiphone");

					//If this fails oh well, that player has done smth wrong in their progression :shrug:
					JsonArray activeContacts = abiphone.get("active_contacts").getAsJsonArray();

					abicaseMp = (int) Math.floor(activeContacts.size() / 2);
				}
			}

			magicalPower += mpToAdd;
		}

		//LOGGER.info("[Aaron's Mod] Acc Report: {} Very Specials, {} Specials, {} Mythics, {} Legendaries, {} Epics, {} Rares, {} Uncommons, {} Commons", verySpecials, specials, mythics, legendaries, epics, rares, uncommons, commons);

		//Sum magical power from bonuses
		magicalPower += hegeMp;
		magicalPower += abicaseMp;

		//Rift Prism MP
		//TODO check if the validaccessories has no rift prism
		if (profile.has("rift")) {
			JsonObject riftAccess = profile.getAsJsonObject("rift").getAsJsonObject("access");

			if (JsonHelper.getBoolean(riftAccess, "consumed_prism").orElse(false)) magicalPower += 11;
		}

		//Selected power
		String selectedPower = JsonHelper.getString(accessoryBagStorage, "selected_power").orElse("None");

		//Get the selected magical power's data and calculate the stat boosts
		Map<String, MagicalPower> magicalPowers = Accessories.getMagicalPowers();
		MagicalPower powerData = magicalPowers.getOrDefault(selectedPower, null);

		Object2FloatOpenHashMap<String> stats = null;
		Object2FloatOpenHashMap<String> bonus = null;

		if (powerData != null) {
			double statsMult = STATS_MULT.applyAsDouble(magicalPower);

			stats = Util.make(powerData.stats().clone(), m -> m.object2FloatEntrySet().stream().forEach(e -> e.setValue((float) Math.round(e.getFloatValue() * statsMult))));
			bonus = powerData.bonus();
		}

		//Tunings
		//Having Map<String, JsonElement> be the type used to break command+click inspection for some reason
		Map<String, JsonElement> tuningData = accessoryBagStorage.getAsJsonObject("tuning").getAsJsonObject("slot_0").asMap();
		List<Component> tunings = tuningData.entrySet().stream().filter(entry -> entry.getValue().getAsInt() != 0)
				.map(entry -> formatTuningStat(entry.getKey(), entry.getValue().getAsInt()))
				.collect(Collectors.toList());

		//Item Rarity Counts - Maybe I'll make this happen later (I'd really need an ItemRarity enum to reduce code duplication/hackiness)
		//List<Text> rarities = collectedAccessories.entrySet().stream().map(entry -> getRarityBreakdownText(entry.getValue(), collectedAccessories)).collect(Collectors.toList());

		int finalMagicalPower = magicalPower;
		Object2FloatOpenHashMap<String> finalStats = stats;
		Object2FloatOpenHashMap<String> finalBonus = bonus;

		RenderHelper.runOnRenderThread(() -> {
			Component startText = Component.literal("     ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
					.append(Component.literal("[- ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
					.append(Component.literal(name).withStyle(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
					.append(Component.literal(" -]").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
					.append(Component.literal("     ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt())).withStyle(style -> style.withStrikethrough(true))));

			source.sendFeedback(startText);

			source.sendFeedback(Component.literal("Magical Power » ").withColor(colourProfile.infoColour.getAsInt())
					.append(Component.literal(Formatters.INTEGER_NUMBERS.format(finalMagicalPower)).withColor(colourProfile.highlightColour.getAsInt())));
			source.sendFeedback(Component.literal("Selected Power » " + Functions.titleCase(selectedPower)).withColor(colourProfile.infoColour.getAsInt()));

			//If the power data isn't null then print out the stats
			if (powerData != null) {
				source.sendFeedback(Component.literal(""));

				finalStats.object2FloatEntrySet().stream()
						.sorted(MagicalPowerCommand::compareStats)
						.map(e -> formatStatText(e.getKey(), e.getFloatValue()))
						.forEachOrdered(source::sendFeedback);

				source.sendFeedback(Component.literal(""));

				if (finalBonus.size() > 0) {
					List<Component> bonuses = finalBonus.clone().object2FloatEntrySet().stream().map(e -> formatStatText(e.getKey(), e.getFloatValue())).collect(Collectors.toList());

					source.sendFeedback(Component.literal("(Unique Power Bonus)").withColor(colourProfile.hoverColour.getAsInt()).withStyle(style -> style.withHoverEvent(
							new HoverEvent.ShowText(getStatsBreakdown(bonuses)))));
				}
			} else {
				source.sendFeedback(Component.literal(""));
			}

			source.sendFeedback(Component.literal("(Tunings)").withStyle(style -> style.withColor(colourProfile.hoverColour.getAsInt()).withHoverEvent(
					new HoverEvent.ShowText(getStatsBreakdown(tunings)))));

			source.sendFeedback(Component.literal(CommandSystem.getEndSpaces(startText)).withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
		});
	}

	private static Component formatTuningStat(String stat, int tuningAmount) {
		return formatStatText(stat, scaleTuningStat(stat, tuningAmount));
	}

	private static Component formatStatText(String stat, float amount) {
		String base = (Math.signum(amount) == 1f ? "+" : "") + Formatters.FLOAT_NUMBERS.format(amount);

		return switch (stat) {
			case "health" -> Component.literal(base + "\u2764 Health").withStyle(ChatFormatting.RED);
			case "defence", "defense" -> Component.literal(base + "\u2748 Defence").withStyle(ChatFormatting.GREEN);
			case "walk_speed" -> Component.literal(base + "\u2726 Speed").withStyle(ChatFormatting.WHITE);
			case "strength" -> Component.literal(base + "\u2741 Strength").withStyle(ChatFormatting.RED);
			case "critical_damage" -> Component.literal(base + "\u2620 Crit Damage").withStyle(ChatFormatting.BLUE);
			case "critical_chance" -> Component.literal(base + "\u2623 Crit Chance").withStyle(ChatFormatting.BLUE);
			case "attack_speed" -> Component.literal(base + "\u2694 Bonus Attack Speed").withStyle(ChatFormatting.YELLOW);
			case "intelligence" -> Component.literal(base + "\u270E Intelligence").withStyle(ChatFormatting.AQUA);
			case "ferocity" -> Component.literal(base + "\u2AFD Ferocity").withStyle(ChatFormatting.RED);
			case "ability_damage" -> Component.literal(base + "\u0E51 Ability Damage").withStyle(ChatFormatting.RED);
			case "true_defence", "true_defense" -> Component.literal(base + "\u2742 True Defence").withStyle(ChatFormatting.WHITE);
			case "combat_wisdom" -> Component.literal(base + "\u262F Combat Wisdom").withStyle(ChatFormatting.DARK_AQUA);
			case "vitality" -> Component.literal(base + "\u2668 Vitality").withStyle(ChatFormatting.DARK_RED);
			case "mending" -> Component.literal(base + "\u2604 Mending").withStyle(ChatFormatting.GREEN);

			default -> Component.literal(base + " " + Functions.titleCase(stat.replace('_', ' '))).withStyle(ChatFormatting.GRAY);
		};
	}

	/**
	 * Used to scale tuning point stats to what the amount of the {@code stat} they actually give per point.
	 */
	private static float scaleTuningStat(String stat, int amount) {
		return switch (stat) {
			case "health" -> amount * 5;
			case "walk_speed" -> amount * 1.5f;
			case "critical_chance" -> amount * 0.2f;
			case "attack_speed" -> amount * 0.3f;
			case "intelligence" -> amount * 2;

			default -> amount;
		};
	}

	private static Component getStatsBreakdown(List<Component> list) {
		MutableComponent breakdown = Component.empty();

		Iterator<Component> iterator = list.iterator();

		while (iterator.hasNext()) {
			MutableComponent statText = (MutableComponent) iterator.next();

			if (iterator.hasNext()) statText.append(Component.literal("\n"));

			breakdown.append(statText);
		}

		return breakdown;
	}

	private static int compareStats(Object o1, Object o2) {
		List<String> order = List.of("health", "defence", "defense", "walk_speed", "strength", "intelligence", "critical_chance", "critical_damage", "attack_speed",
				"ability_damage", "true_defence", "true_defense", "ferocity", "vitality", "mending", "combat_wisdom");
		Comparator<String> comparator = (s1, s2) -> Integer.compare(order.indexOf(s1), order.indexOf(s2));

		return comparator.compare((String) Object2FloatMap.Entry.class.cast(o1).getKey(), (String) Object2FloatMap.Entry.class.cast(o2).getKey());
	}

	@SuppressWarnings("unused")
	private static Component getRarityBreakdownText(String rarity, Object2ObjectOpenHashMap<String, String> accessoryMap) {
		int count = countOfRarity(accessoryMap, rarity);
		int mpPer = switch (rarity) {
			case "VERY SPECIAL" -> 5;
			case "SPECIAL" -> 3;
			case "MYTHIC" -> 22;
			case "LEGENDARY" -> 16;
			case "EPIC" -> 12;
			case "RARE" -> 8;
			case "UNCOMMON" -> 5;
			case "COMMON" -> 3;

			default -> throw new IllegalArgumentException("Unexpected value: " + rarity);
		};

		return null;
	}

	private static int countOfRarity(Object2ObjectOpenHashMap<String, String> map, String rarity) {
		return (int) map.entrySet().stream().filter(entry -> entry.getValue().equals(rarity)).count();
	}
}

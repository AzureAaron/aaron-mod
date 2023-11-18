package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.azureaaron.mod.util.Constants.WITH_COLOUR;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.IntToDoubleFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.JsonHelper;
import net.azureaaron.mod.util.Messages;
import net.azureaaron.mod.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MagicalPowerCommand {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final MethodHandle DISPATCH_HANDLE = CommandSystem.obtainDispatchHandle4Skyblock("printMP");
	private static final Text NO_ACCESSORY_BAG_DATA = Text.literal("This profile doesn't have any accessory bag data!");
	private static final Text NBT_PARSING_ERROR = Text.literal("There was an error while trying to parse NBT!").styled(style -> style.withColor(Formatting.RED)); //TODO make constant
	private static final Pattern ACCESSORY_RARITY_PATTERN = Pattern.compile("(?:a )?(?<rarity>(?:VERY )?[A-Za-z]+) (?:DUNGEON )?(?:A|HAT)CCESSORY(?: a)?");
	@SuppressWarnings("unused")
	private static final IntToDoubleFunction STATS_MULT = magicalPower -> 29.97d * Math.pow(Math.log(0.0019d * magicalPower + 1d), 1.2d);
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("magicalpower")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
		
		dispatcher.register(literal("mp")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
	}
	
	//TODO maybe make this account for when you can't use an accessory
	//TODO also display the stats you get from your power
	protected static void printMP(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		JsonObject profile = body.getAsJsonObject("members").getAsJsonObject(uuid);	
		
		JsonObject inventoryData = profile.getAsJsonObject("inventory");
		boolean inventoryEnabled = Skyblock.isInventoryApiEnabled(inventoryData);
		
		if (!inventoryEnabled) {
			source.sendError(Messages.INVENTORY_API_DISABLED_ERROR);
			
			return;
		}
		
		JsonObject accessoryBagStorage = profile.getAsJsonObject("accessory_bag_storage");
		
		if (accessoryBagStorage == null) {
			source.sendError(NO_ACCESSORY_BAG_DATA);
			
			return;
		}
		
		String accessoriesItemData = JsonHelper.getString(inventoryData, "bag_contents.talisman_bag.data").orElseThrow();
		NbtList accessories;
		
		try {
			accessories = NbtIo.readCompressed(new ByteArrayInputStream(Base64.getDecoder().decode(accessoriesItemData))).getList("i", NbtElement.COMPOUND_TYPE);
		} catch (IOException e) {
			source.sendError(NBT_PARSING_ERROR);
			LOGGER.error("[Aaron's Mod] Encountered an exception while parsing NBT!", e);
			
			return;
		}
		
		//Map accessory id -> rarity
		Object2ObjectOpenHashMap<String, String> collectedAccessories = new Object2ObjectOpenHashMap<>();
				
		//Loop through the accessories
		for (int i = 0; i < accessories.size(); i++) {
			NbtCompound tag = accessories.getCompound(i).getCompound("tag");
			
			String itemId = "";
			
			//Collect item id of all accessories
			if (tag.contains("ExtraAttributes")) {
				NbtCompound extraAttributes = tag.getCompound("ExtraAttributes");
				
				if (extraAttributes.contains("id")) itemId = extraAttributes.getString("id");
			}
			
			NbtCompound display = tag.getCompound("display");
			NbtList lore = display.getList("Lore", NbtElement.STRING_TYPE);
						
			//Determine the rarity - iterate backwards for efficiency
			for (int i2 = lore.size(); i2 >= 0; i2--) {
				String loreLine = Formatting.strip(lore.getString(i2));
				Matcher matcher = ACCESSORY_RARITY_PATTERN.matcher(loreLine);
				
				if (!itemId.isBlank() && matcher.matches()) {
					collectedAccessories.put(itemId, matcher.group("rarity"));
										
					break;
				}
			}
		}
		
		int magicalPower = 0;
		int hegeMp = 0;
		int abicaseMp = 0;
		
		//Calculate the magical power!
		for (Map.Entry<String, String> accessory : collectedAccessories.entrySet()) {
			String id = accessory.getKey();
			String rarity = accessory.getValue();
			
			int mpToAdd = 0;
			
			switch (rarity) {
				case "VERY SPECIAL" -> mpToAdd = 5;
				case "SPECIAL" -> mpToAdd = 3;
				case "MYTHIC" -> mpToAdd = 22;
				case "LEGENDARY" -> mpToAdd = 16;
				case "EPIC" -> mpToAdd = 12;
				case "RARE" -> mpToAdd = 8;
				case "UNCOMMON" -> mpToAdd = 5;
				case "COMMON" -> mpToAdd = 3;
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
		
		//Sum magical power from bonuses
		magicalPower += hegeMp;
		magicalPower += abicaseMp;
		
		//Rift Prism MP
		if (profile.has("rift")) {
			JsonObject riftAccess = profile.getAsJsonObject("rift").getAsJsonObject("access");
			
			if (JsonHelper.getBoolean(riftAccess, "consumed_prism").orElse(false)) magicalPower += 11;
		}
		
		//Selected power & tunings
		String selectedPower = JsonHelper.getString(accessoryBagStorage, "selected_power").orElse("None");
		
		//Tunings
		var tuningData = accessoryBagStorage.getAsJsonObject("tuning").getAsJsonObject("slot_0").asMap(); //Having Map<String, JsonElement> be the type broke command+click inspection for some reason
		List<Text> tunings = tuningData.entrySet().stream().filter(entry -> entry.getValue().getAsInt() != 0)
				.map(entry -> getStatText(entry.getKey(), entry.getValue().getAsInt()))
				.collect(Collectors.toList());
		
		//Item Rarity Counts - Maybe I'll make this happen later (I'd really need an ItemRarity enum to reduce code duplication/hackiness)
		//List<Text> rarities = collectedAccessories.entrySet().stream().map(entry -> getRarityBreakdownText(entry.getValue(), collectedAccessories)).collect(Collectors.toList());
				
		Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true))));
		
		source.sendFeedback(startText);
		
		source.sendFeedback(Text.literal("Magical Power » ").styled(WITH_COLOUR.apply(colourProfile.infoColour))
				.append(Text.literal(Functions.NUMBER_FORMATTER_ND.format(magicalPower)).styled(WITH_COLOUR.apply(colourProfile.highlightColour))));
		source.sendFeedback(Text.literal("Selected Power » " + Functions.titleCase(selectedPower)).styled(WITH_COLOUR.apply(colourProfile.infoColour)));
		
		source.sendFeedback(Text.literal(""));
		
		source.sendFeedback(Text.literal("(Tunings)").styled(style -> style.withColor(colourProfile.hoverColour).withHoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, getStatsBreakdown(tunings)))));
		
		source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
	}
	
	private static Text getStatText(String stat, int tuningAmount) {
		String base = "+" + Functions.NUMBER_FORMATTER_OD.format(scaleTuningStat(stat, tuningAmount));
		
		return switch (stat) {
			case "healh" -> Text.literal(base + "\u2764 Health").formatted(Formatting.RED);
			case "defense" -> Text.literal(base + "\u2748 Defence").formatted(Formatting.GREEN);
			case "walk_speed" -> Text.literal(base + "\u2726 Speed").formatted(Formatting.WHITE);
			case "strength" -> Text.literal(base + "\u2741 Strength").formatted(Formatting.RED);
			case "critical_damage" -> Text.literal(base + "\u2620 Crit Damage").formatted(Formatting.BLUE);
			case "critical_chance" -> Text.literal(base + "\u2623 Crit Chance").formatted(Formatting.BLUE);
			case "attack_speed" -> Text.literal(base + "\u2694 Bonus Attack Speed").formatted(Formatting.YELLOW);
			case "intelligence" -> Text.literal(base + "\u270E Intelligence").formatted(Formatting.AQUA);
			
			default -> Text.empty();
		};
	}
	
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
	
	private static Text getStatsBreakdown(List<Text> list) {
		MutableText breakdown = Text.empty();
		
		Iterator<Text> iterator = list.iterator();
		
		while (iterator.hasNext()) {
			MutableText statText = (MutableText) iterator.next();
			
			if (iterator.hasNext()) statText.append(Text.literal("\n"));
			
			breakdown.append(statText);
		}
		
		return breakdown;
	}
	
	@SuppressWarnings("unused")
	private static Text getRarityBreakdownText(String rarity, Object2ObjectOpenHashMap<String, String> accessoryMap) {
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

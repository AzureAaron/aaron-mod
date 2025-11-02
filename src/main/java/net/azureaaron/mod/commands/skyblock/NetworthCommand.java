package net.azureaaron.mod.commands.skyblock;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.commands.Command;
import net.azureaaron.mod.commands.CommandSystem;
import net.azureaaron.mod.commands.SkyblockCommand;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.Formatters;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.ItemUtils;
import net.azureaaron.mod.utils.JsonHelper;
import net.azureaaron.mod.utils.Messages;
import net.azureaaron.mod.utils.Skyblock;
import net.azureaaron.mod.utils.networth.NetworthCalculator;
import net.azureaaron.mod.utils.render.RenderHelper;
import net.azureaaron.networth.PetCalculator;
import net.azureaaron.networth.item.PetInfo;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NetworthCommand extends SkyblockCommand {
	private static final Command INSTANCE = new NetworthCommand();
	private static final Logger LOGGER = LogUtils.getLogger();

	@Init
	public static void init() {
		if (AaronModConfigManager.get().skyblock.commands.enableSkyblockCommands) ClientCommandRegistrationCallback.EVENT.register(INSTANCE::register);
	}

	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(literal("networth")
				.executes(context -> CommandSystem.handleSelf4Skyblock(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(this, context.getSource(), getString(context, "player")))));
		
		dispatcher.register(literal("nw")
				.executes(context -> CommandSystem.handleSelf4Skyblock(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(this, context.getSource(), getString(context, "player")))));
	}

	@Override
	public void print(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		ColourProfiles colourProfile = Constants.PROFILE.get();

		JsonObject profile = body.getAsJsonObject("members").getAsJsonObject(uuid);
		boolean inventoryEnabled = Skyblock.isInventoryApiEnabled(JsonHelper.getObject(profile, "inventory").orElseGet(JsonObject::new));

		if (!inventoryEnabled) {
			source.sendError(Messages.INVENTORY_API_DISABLED_ERROR.get());

			return;
		}

		Object2DoubleMap<ItemStack> armour = new Object2DoubleOpenHashMap<>();
		Object2DoubleMap<ItemStack> equipment = new Object2DoubleOpenHashMap<>();
		Object2DoubleMap<ItemStack> inventory = new Object2DoubleOpenHashMap<>();
		Object2DoubleMap<ItemStack> enderChests = new Object2DoubleOpenHashMap<>();
		Object2DoubleMap<ItemStack> storage = new Object2DoubleOpenHashMap<>();
		Object2DoubleMap<ItemStack> accessories = new Object2DoubleOpenHashMap<>();
		Object2DoubleMap<ItemStack> wardrobe = new Object2DoubleOpenHashMap<>();
		Object2DoubleMap<PetInfo> pets = new Object2DoubleOpenHashMap<>();
		Object2DoubleMap<String> essence = new Object2DoubleOpenHashMap<>();
		Object2DoubleMap<String> sacks = new Object2DoubleOpenHashMap<>();

		try {
			armour.putAll(calculateNetworth4Items(ItemUtils.parseCompressedItemData(JsonHelper.getString(profile, "inventory.inv_armor.data").orElseThrow())));
			equipment.putAll(calculateNetworth4Items(ItemUtils.parseCompressedItemData(JsonHelper.getString(profile, "inventory.equipment_contents.data").orElseThrow())));
			inventory.putAll(calculateNetworth4Items(ItemUtils.parseCompressedItemData(JsonHelper.getString(profile, "inventory.inv_contents.data").orElseThrow())));
			enderChests.putAll(calculateNetworth4Items(ItemUtils.parseCompressedItemData(JsonHelper.getString(profile, "inventory.ender_chest_contents.data").orElseThrow())));
			accessories.putAll(calculateNetworth4Items(ItemUtils.parseCompressedItemData(JsonHelper.getString(profile, "inventory.bag_contents.talisman_bag.data").orElseThrow())));
			wardrobe.putAll(calculateNetworth4Items(ItemUtils.parseCompressedItemData(JsonHelper.getString(profile, "inventory.wardrobe_contents.data").orElseThrow())));

			//Backpacks
			JsonObject backpacks = JsonHelper.getObject(profile, "inventory.backpack_contents").orElseGet(JsonObject::new);

			for (String key : backpacks.keySet()) {
				String backpackData = JsonHelper.getString(backpacks, key + ".data").orElseThrow();
				Object2DoubleMap<ItemStack> calculations = calculateNetworth4Items(ItemUtils.parseCompressedItemData(backpackData));

				storage.putAll(calculations);
			}

			//Pets
			JsonArray profilePets = JsonHelper.getArray(profile, "pets_data.pets").orElseGet(JsonArray::new);

			//The cast is needed to avoid a compiler error
			pets.putAll((Object2DoubleMap<PetInfo>) profilePets.asList().stream()
					.map(JsonElement::getAsJsonObject)
					.map(JsonHelper::clearNullValues)
					.map(petInfo -> new Dynamic<>(JsonOps.INSTANCE, petInfo))
					.map(PetInfo.CODEC::parse)
					.map(DataResult::getOrThrow)
					.collect(Collectors.toMap(Function.identity(), petInfo -> NetworthCalculator.calculatePetNetworth(petInfo).price(), (a, b) -> a, Object2DoubleOpenHashMap::new)));

			//Essence
			JsonObject essences = JsonHelper.getObject(profile, "currencies.essence").orElseGet(JsonObject::new);

			for (String essenceType : essences.keySet()) {
				int amount = JsonHelper.getInt(essences, essenceType + ".current").orElse(0);

				essence.put(essenceType, NetworthCalculator.calculateEssenceNetworth(essenceType, amount).price());
			}

			//Sacks
			JsonObject sacksItems = JsonHelper.getObject(profile, "inventory.sacks_counts").orElseGet(JsonObject::new);

			for (Map.Entry<String, JsonElement> sackItem : sacksItems.entrySet()) {
				int amount = sackItem.getValue().getAsInt();

				sacks.put(sackItem.getKey(), NetworthCalculator.calculateSackItemNetworth(sackItem.getKey(), amount).price());
			}
		} catch (Exception e) {
			LOGGER.error("[Aaron's Mod Networth Command] Failed to calculate networth :(", e);
			source.sendError(Constants.PREFIX.get().append(Text.literal("Failed to calculate this player's networth!").formatted(Formatting.RED)));

			return;
		}

		double armourValue = armour.values().doubleStream().sum();
		double equipmentValue = equipment.values().doubleStream().sum();
		double inventoryValue = inventory.values().doubleStream().sum();
		double enderChestsValue = enderChests.values().doubleStream().sum();
		double storageValue = storage.values().doubleStream().sum();
		double accessoriesValue = accessories.values().doubleStream().sum();
		double petsValue = pets.values().doubleStream().sum();
		double wardrobeValue = wardrobe.values().doubleStream().sum();
		double sacksValue = sacks.values().doubleStream().sum();
		double essenceValue = essence.values().doubleStream().sum();

		double purse = JsonHelper.getDouble(profile, "currencies.coin_purse").orElse(0d);
		double bank = JsonHelper.getDouble(body, "banking.balance").orElse(0d);

		double overallValue = armourValue + equipmentValue + inventoryValue + enderChestsValue + storageValue + accessoriesValue + petsValue + wardrobeValue + sacksValue + essenceValue + purse + bank;

		RenderHelper.runOnRenderThread(() -> {
			Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
					.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
					.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
					.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
					.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt())).styled(style -> style.withStrikethrough(true))));

			source.sendFeedback(startText);

			source.sendFeedback(Text.literal("Networth » " + Formatters.INTEGER_NUMBERS.format(overallValue) + " (" + Formatters.SHORT_FLOAT_NUMBERS.format(overallValue) + ")").withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Text.literal(""));
			source.sendFeedback(Text.literal("Armour » " + Formatters.SHORT_FLOAT_NUMBERS.format(armourValue)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(armourValue, armour, ItemStack::getName))));
			source.sendFeedback(Text.literal("Equipment » " + Formatters.SHORT_FLOAT_NUMBERS.format(equipmentValue)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(equipmentValue, equipment, ItemStack::getName))));
			source.sendFeedback(Text.literal("Inventory » " + Formatters.SHORT_FLOAT_NUMBERS.format(inventoryValue)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(inventoryValue, inventory, ItemStack::getName))));
			source.sendFeedback(Text.literal("Ender Chests » " + Formatters.SHORT_FLOAT_NUMBERS.format(enderChestsValue)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(enderChestsValue, enderChests, ItemStack::getName))));
			source.sendFeedback(Text.literal("Storage » " + Formatters.SHORT_FLOAT_NUMBERS.format(storageValue)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(storageValue, storage, ItemStack::getName))));
			source.sendFeedback(Text.literal("Accessories » " + Formatters.SHORT_FLOAT_NUMBERS.format(accessoriesValue)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(accessoriesValue, accessories, ItemStack::getName))));
			source.sendFeedback(Text.literal("Pets » " + Formatters.SHORT_FLOAT_NUMBERS.format(petsValue)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(petsValue, pets, NetworthCommand::formatPet))));
			source.sendFeedback(Text.literal("Wardrobe » " + Formatters.SHORT_FLOAT_NUMBERS.format(wardrobeValue)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(wardrobeValue, wardrobe, ItemStack::getName))));
			source.sendFeedback(Text.literal("Sacks » " + Formatters.SHORT_FLOAT_NUMBERS.format(sacksValue)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(sacksValue, sacks, NetworthCommand::formatSacks))));
			source.sendFeedback(Text.literal("Essence » " + Formatters.SHORT_FLOAT_NUMBERS.format(essenceValue)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(essenceValue, essence, NetworthCommand::formatEssence))));

			source.sendFeedback(Text.literal(""));
			source.sendFeedback(Text.literal("Bank » " + Formatters.SHORT_FLOAT_NUMBERS.format(bank)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(bank, null, null))));
			source.sendFeedback(Text.literal("Purse » " + Formatters.SHORT_FLOAT_NUMBERS.format(purse)).withColor(colourProfile.infoColour.getAsInt()).styled(style -> style.withHoverEvent(getHover(purse, null, null))));

			source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
		});
	}

	private static Object2DoubleMap<ItemStack> calculateNetworth4Items(List<ItemStack> items) {
		Object2DoubleMap<ItemStack> calculations = new Object2DoubleOpenHashMap<>();

		for (ItemStack stack : items) {
			if (!stack.isEmpty()) calculations.put(stack, NetworthCalculator.calculateItemNetworth(stack).price());
		}

		return calculations;
	}

	private static <T> HoverEvent getHover(double totalValue, @Nullable Object2DoubleMap<T> objects, @Nullable Function<T, Text> textifier) {
		ColourProfiles colourProfile = Constants.PROFILE.get();
		MutableText hoverText = Text.empty().append(Text.literal("Exact Value » " + Formatters.INTEGER_NUMBERS.format(totalValue)).withColor(colourProfile.infoColour.getAsInt()));

		if (objects != null && textifier != null) {
			List<Object2DoubleMap.Entry<T>> sortedEntries = objects.object2DoubleEntrySet().stream()
					.sorted(Comparator.comparingDouble(Object2DoubleMap.Entry::getDoubleValue))
					.toList()
					.reversed();

			for (Object2DoubleMap.Entry<T> entry : sortedEntries.subList(0, Math.min(sortedEntries.size(), 5))) {
				hoverText.append("\n");
				hoverText.append(textifier.apply(entry.getKey()));
				hoverText.append(" - ");
				hoverText.append(Text.literal(Formatters.SHORT_FLOAT_NUMBERS.format(entry.getDoubleValue())).withColor(colourProfile.infoColour.getAsInt()));
			}
		}

		return new HoverEvent.ShowText(hoverText);
	}

	private static Text formatPet(PetInfo petInfo) {
		Formatting nameColour = switch (petInfo.tier()) {
			case "MYTHIC" -> Formatting.LIGHT_PURPLE;
			case "LEGENDARY" -> Formatting.GOLD;
			case "EPIC" -> Formatting.DARK_PURPLE;
			case "RARE" -> Formatting.BLUE;
			case "UNCOMMON" -> Formatting.GREEN;
			case "COMMON" -> Formatting.WHITE;

			default -> Formatting.GRAY;
		};
		String formattedName = Functions.titleCase(petInfo.type().replace('_', ' '));
		MutableText text = Text.empty()
				.append(Text.literal("[Lvl " + PetCalculator.calculatePetLevel(petInfo).leftInt() + "] ").formatted(Formatting.GRAY))
				.append(Text.literal(formattedName).formatted(nameColour));

		return text;
	}

	private static Text formatSacks(String itemId) {
		String name = Cache.ITEM_NAMES.get(itemId);

		return name != null ? Text.literal(name) : Text.literal(Functions.titleCase(itemId.replace('_', ' ')));
	}

	private static Text formatEssence(String essenceType) {
		int essenceColour = switch (essenceType) {
			case "WITHER" -> 0x2b2b2a;
			case "SPIDER" -> 0x4b647a;
			case "UNDEAD" -> 0xbf7a8e;
			case "DRAGON" -> 0xf52f2f;
			case "GOLD" -> 0xedbd39;
			case "DIAMOND" -> 0x75ecf0;
			case "ICE" -> 0x94d6f7;
			case "CRIMSON" -> 0xf26411;

			default -> 0;
		};

		return Text.literal(Functions.titleCase(essenceType)).withColor(essenceColour).append(Text.literal(" Essence"));
	}
}

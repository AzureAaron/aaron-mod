package net.azureaaron.mod.commands.skyblock;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.commands.Command;
import net.azureaaron.mod.commands.CommandSystem;
import net.azureaaron.mod.commands.SkyblockCommand;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Constants;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class InventoryCommand extends SkyblockCommand {
	private static final Command INSTANCE = new InventoryCommand();
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Supplier<MutableComponent> NBT_PARSING_ERROR = () -> Constants.PREFIX.get().append(Component.literal("There was an error while trying to parse NBT!").withStyle(ChatFormatting.RED));

	@Init
	public static void init() {
		if (AaronModConfigManager.get().skyblock.commands.enableSkyblockCommands) ClientCommandRegistrationCallback.EVENT.register(INSTANCE::register);
	}

	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("inventory")
				.executes(context -> CommandSystem.handleSelf4Skyblock(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> SharedSuggestionProvider.suggest(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(this, context.getSource(), getString(context, "player")))));

		dispatcher.register(literal("inv")
				.executes(context -> CommandSystem.handleSelf4Skyblock(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> SharedSuggestionProvider.suggest(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(this, context.getSource(), getString(context, "player")))));
	}

	private record ItemData4(ItemStack stack, String fallback) {
		private MutableComponent feedbackMessage() {
			if (!stack.isEmpty()) {
				return stack.getHoverName().copy().withStyle(style -> style.withHoverEvent(new HoverEvent.ShowItem(stack)));
			} else {
				return Component.literal(fallback).withStyle(ChatFormatting.RED);
			}
		}
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

		List<ItemStack> armour = null;
		List<ItemStack> inventory = null;
		List<ItemStack> equipment = null;

		try {
			armour = ItemUtils.parseCompressedItemData(JsonHelper.getString(inventoryData, "inv_armor.data").orElseThrow());
			inventory = ItemUtils.parseCompressedItemData(JsonHelper.getString(inventoryData, "inv_contents.data").orElseThrow());
			equipment = ItemUtils.parseCompressedItemData(JsonHelper.getString(inventoryData, "equipment_contents.data").orElseThrow());
		} catch (IOException | NullPointerException e) {
			source.sendError(NBT_PARSING_ERROR.get());
			LOGGER.error("[Aaron's Mod] Encountered an exception while parsing NBT!", e);

			return;
		}

		ItemData4 boots = new ItemData4(armour.get(0), "No boots equipped!");
		ItemData4 leggings = new ItemData4(armour.get(1), "No leggings equipped!"); //I originally misspelled leggings as beggings.
		ItemData4 chestplate = new ItemData4(armour.get(2), "No chestplate equipped!");
		ItemData4 helmet = new ItemData4(armour.get(3), "No helmet equipped!");

		ItemData4[] equipmentPieces = new ItemData4[4];

		if (equipment != null) {
			equipmentPieces[0] = new ItemData4(equipment.get(0), "No necklace equipped!");
			equipmentPieces[1] = new ItemData4(equipment.get(1), "No cloak equipped!");
			equipmentPieces[2] = new ItemData4(equipment.get(2), "No belt equipped!");
			equipmentPieces[3] = new ItemData4(equipment.get(3), "No gloves or bracelet equipped!");
		}

		ObjectArrayList<ItemData4> keyItems = new ObjectArrayList<>();

		if (inventoryEnabled) {
			for (ItemStack stack : inventory) {
				String itemId = ItemUtils.getId(stack);

				if (itemId.equals("ASTRAEA") || itemId.equals("HYPERION") || itemId.equals("SCYLLA") || itemId.equals("VALKYRIE")
						|| itemId.equals("TERMINATOR") || itemId.equals("DARK_CLAYMORE")) keyItems.add(new ItemData4(stack, "Error parsing item :("));
			}
		}

		//Sort key items by name
		keyItems.sort(Comparator.comparing(id -> id.stack().getHoverName().getString()));
		List<ItemStack> equipmentFinal = equipment;

		RenderHelper.runOnRenderThread(() -> {
			Component startText = Component.literal("     ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
					.append(Component.literal("[- ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
					.append(Component.literal(name).withStyle(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
					.append(Component.literal(" -]").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
					.append(Component.literal("     ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt())).withStyle(style -> style.withStrikethrough(true))));

			source.sendFeedback(startText);

			source.sendFeedback(Component.literal("Inventory API » " + ((inventoryEnabled) ? "✓" : "✗")).withColor(colourProfile.infoColour.getAsInt()));
			source.sendFeedback(Component.literal(""));
			source.sendFeedback(helmet.feedbackMessage());
			source.sendFeedback(chestplate.feedbackMessage());
			source.sendFeedback(leggings.feedbackMessage());
			source.sendFeedback(boots.feedbackMessage());

			if (equipmentFinal != null) {
				source.sendFeedback(Component.literal(""));

				source.sendFeedback(equipmentPieces[0].feedbackMessage());
				source.sendFeedback(equipmentPieces[1].feedbackMessage());
				source.sendFeedback(equipmentPieces[2].feedbackMessage());
				source.sendFeedback(equipmentPieces[3].feedbackMessage());
			}

			//Print feedback
			if (keyItems.size() > 0) {
				source.sendFeedback(Component.literal(""));

				for (ItemData4 item : keyItems) {
					source.sendFeedback(item.feedbackMessage());
				}
			}

			source.sendFeedback(Component.literal(CommandSystem.getEndSpaces(startText)).withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
		});
	}
}

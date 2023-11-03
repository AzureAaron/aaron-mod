package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.io.IOException;
import java.lang.invoke.MethodHandle;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import net.azureaaron.mod.util.ItemUtils;
import net.azureaaron.mod.util.TextTransformer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.HoverEvent.ItemStackContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class InventoryCommand {
	private static final MethodHandle DISPATCH_HANDLE = CommandSystem.obtainDispatchHandle4Skyblock("printInventory");
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("inventory")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
		
		dispatcher.register(literal("inv")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
	}
	
	private static final Text NBT_PARSING_ERROR = Text.literal("There was an error while trying to parse NBT!").styled(style -> style.withColor(Formatting.RED));
	
	private record ItemData3(String name, NBTCompound nbt) {
		
		private MutableText formattedName() {
			return TextTransformer.fromLegacy(name);
		}
		
		/**
		 * Calling this when the item wasn't found will throw an exception
		 */
		private Text[] formattedLore() {
			return nbt.getList("tag.display.Lore").stream().map(element -> TextTransformer.fromLegacy((String) element)).toArray(Text[]::new);
		}
		
		private IntIntPair getIdAndDamage() {
			return IntIntPair.of(nbt.getInt("id", 0), nbt.getInt("Damage", 0));
		}
		
		private String getItemId() {
			return nbt.getString("tag.ExtraAttributes.id", "UNKNOWN");
		}
		
		private ItemStack getStack() {
			IntIntPair idAndDmg = getIdAndDamage();
			String sbId = getItemId();
			String timestamp = nbt.getString("tag.ExtraAttributes.timestamp", "1/1/70 12:00 AM");
			String uuid = nbt.getString("tag.ExtraAttributes.uuid", "UNKNOWN");
			
			NbtCompound extraAttributes = new NbtCompound();
			extraAttributes.putString("id", sbId);
			extraAttributes.putString("timestamp", timestamp);
			extraAttributes.putString("uuid", uuid);
			
			return ItemUtils.createStack(ItemUtils.identifierFromOldId(idAndDmg.leftInt(), idAndDmg.rightInt()), formattedName(), formattedLore(), extraAttributes);
		}
		
		private MutableText feedbackMessage() {
			if (formattedName().getString().endsWith("equipped!")) return formattedName();
			
			ItemStack stack = getStack();
			MutableText name = (MutableText) stack.getName().copy();
			
			return name.styled(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_ITEM, new ItemStackContent(stack))));
		}
	};
	
	protected static void printInventory(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		JsonObject profile = body.get("members").getAsJsonObject().get(uuid).getAsJsonObject();
		String endSpaces = "        " + name.replaceAll("[A-z0-9_]", "  ") + "        ";
		
		boolean inventoryEnabled = (profile.get("inv_contents") != null) ? true : false;
		
		NBTList armour = null;
		NBTList inventory = null;
		NBTList equipment = null;
		
		try {
			String armourContents = profile.get("inv_armor").getAsJsonObject().get("data").getAsString();
			armour = NBTReader.readBase64(armourContents).getList("i");
			
			if(inventoryEnabled) {
				String inventoryContents = profile.get("inv_contents").getAsJsonObject().get("data").getAsString();
				inventory = NBTReader.readBase64(inventoryContents).getList("i");
				
				String equipmentContents = profile.get("equippment_contents").getAsJsonObject().get("data").getAsString();
				equipment = NBTReader.readBase64(equipmentContents).getList("i");
			}
		} catch (IOException | NullPointerException e) {
			source.sendError(NBT_PARSING_ERROR);
			e.printStackTrace();
			return;
		}
		
		//TODO eventually support fancy dia heads & old master stars
		
		ItemData3 boots = new ItemData3(
				armour.getCompound(0).getString("tag.display.Name", "§cNo boots equipped!"), 
				armour.getCompound(0));
		ItemData3 leggings = new ItemData3(
				armour.getCompound(1).getString("tag.display.Name", "§cNo leggings equipped!"), //I originally misspelled leggings as beggings.
				armour.getCompound(1));
		ItemData3 chestplate = new ItemData3(
				armour.getCompound(2).getString("tag.display.Name", "§cNo chestplate equipped!"), 
				armour.getCompound(2));
		ItemData3 helmet = new ItemData3(
				armour.getCompound(3).getString("tag.display.Name", "§cNo helmet equipped!"), 
				armour.getCompound(3));
		
		ItemData3[] equipmentPieces = new ItemData3[4];
		
		if (equipment != null) {			
			equipmentPieces[0] = new ItemData3(
					equipment.getCompound(0).getString("tag.display.Name", "§cNo necklace equipped!"), 
					equipment.getCompound(0));
			equipmentPieces[1] = new ItemData3(
					equipment.getCompound(1).getString("tag.display.Name", "§cNo cloak equipped!"), 
					equipment.getCompound(1));
			
			equipmentPieces[2] = new ItemData3(
					equipment.getCompound(2).getString("tag.display.Name", "§cNo belt equipped!"), 
					equipment.getCompound(2));
			
			equipmentPieces[3] = new ItemData3(
					equipment.getCompound(3).getString("tag.display.Name", "§cNo gloves or bracelet equipped!"), 
					equipment.getCompound(3));
		}
		
		ObjectArrayList<ItemData3> keyItems = new ObjectArrayList<>();
		
		if (inventoryEnabled) {
			for (int i = 0; i < 36; i++) {
				NBTCompound item = inventory.getCompound(i);
				String itemId = item.getString("tag.ExtraAttributes.id", "NONE");
				
				if (itemId.equals("ASTRAEA") || itemId.equals("HYPERION") || itemId.equals("SCYLLA") || itemId.equals("VALKYRIE")
						|| itemId.equals("TERMINATOR") || itemId.equals("DARK_CLAYMORE")) keyItems.add(new ItemData3(item.getString("tag.display.Name"), item));
			}
		}
		
		//Sort key items by name
		keyItems.sort((o1, o2) -> o1.formattedName().getString().compareTo(o2.formattedName().getString()));
		
		source.sendFeedback(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true)))));
		
		source.sendFeedback(Text.literal("Inventory API » " + ((inventoryEnabled) ? "✓" : "✗")).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal(""));
		source.sendFeedback(helmet.feedbackMessage());
		source.sendFeedback(chestplate.feedbackMessage());
		source.sendFeedback(leggings.feedbackMessage());
		source.sendFeedback(boots.feedbackMessage());
		
		if (equipment != null) {
			source.sendFeedback(Text.literal(""));
			
			source.sendFeedback(equipmentPieces[0].feedbackMessage());
			source.sendFeedback(equipmentPieces[1].feedbackMessage());
			source.sendFeedback(equipmentPieces[2].feedbackMessage());
			source.sendFeedback(equipmentPieces[3].feedbackMessage());
		}
		
		//Print feedback
		if (keyItems.size() > 0) {
			source.sendFeedback(Text.literal(""));
			
			for (ItemData3 item : keyItems) {
				source.sendFeedback(item.feedbackMessage());
			}
		}
		
		source.sendFeedback(Text.literal(endSpaces).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
		return;
	}
}

package net.azureaaron.mod.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import net.azureaaron.mod.utils.datafixer.LegacyItemStackFixer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class ItemUtils {
	private static final Minecraft CLIENT = Minecraft.getInstance();
	private static final Provider DEFAULT_LOOKUP = VanillaRegistries.createLookup();

	/**
	 * Decodes from the item format from the Hypixel API into a list of {@link CompoundTag}s.
	 */
	public static List<CompoundTag> decodeCompressedItemData(String encoded) throws IOException {
		return decodeCompressedItemData(Base64.getDecoder().decode(encoded));
	}

	/**
	 * @see #decodeCompressedItemData(String)
	 */
	public static List<CompoundTag> decodeCompressedItemData(byte[] bytes) throws IOException {
		return NbtIo.readCompressed(new ByteArrayInputStream(bytes), NbtAccounter.unlimitedHeap()).getListOrEmpty("i").stream()
				.map(CompoundTag.class::cast)
				.toList();
	}

	/**
	 * Parses the item format from the Hypixel API into a list of {@link ItemStack}s.
	 */
	public static List<ItemStack> parseCompressedItemData(String encoded) throws IOException {
		return decodeCompressedItemData(encoded).stream()
				.map(LegacyItemStackFixer::fixLegacyStack)
				.toList();
	}

	public static CompoundTag getCustomData(DataComponentHolder stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}

	public static String getId(DataComponentHolder stack) {
		return getCustomData(stack).getStringOr("id", "");
	}

	public static Provider getRegistryLookup() {
		return CLIENT.getConnection() != null && CLIENT.getConnection().registryAccess() != null ? CLIENT.getConnection().registryAccess() : DEFAULT_LOOKUP;
	}
}

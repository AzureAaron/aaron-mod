package net.azureaaron.mod.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import net.azureaaron.mod.utils.datafixer.LegacyItemStackFixer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class ItemUtils {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final WrapperLookup DEFAULT_LOOKUP = BuiltinRegistries.createWrapperLookup();

	/**
	 * Parses the item format from the Hypixel API into a list of {@link ItemStack}s.
	 */
	public static List<ItemStack> parseCompressedItemData(String encoded) throws IOException {
		List<ItemStack> items = NbtIo.readCompressed(new ByteArrayInputStream(Base64.getDecoder().decode(encoded)), NbtSizeTracker.ofUnlimitedBytes()).getList("i", NbtElement.COMPOUND_TYPE).stream()
				.map(NbtCompound.class::cast)
				.map(LegacyItemStackFixer::fixLegacyStack)
				.toList();

		return items;
	}

	public static WrapperLookup getRegistryLookup() {
		return CLIENT.getNetworkHandler() != null && CLIENT.getNetworkHandler().getRegistryManager() != null ? CLIENT.getNetworkHandler().getRegistryManager() : DEFAULT_LOOKUP;
	}
}

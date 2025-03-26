package net.azureaaron.mod.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.azureaaron.mod.utils.datafixer.LegacyItemStackFixer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class ItemUtils {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final WrapperLookup DEFAULT_LOOKUP = BuiltinRegistries.createWrapperLookup();

	/**
	 * Decodes from the item format from the Hypixel API into a list of {@link NbtCompound}s.
	 */
	public static List<NbtCompound> decodeCompressedItemData(String encoded) throws IOException {
		return decodeCompressedItemData(Base64.getDecoder().decode(encoded));
	}

	/**
	 * @see #decodeCompressedItemData(String)
	 */
	public static List<NbtCompound> decodeCompressedItemData(byte[] bytes) throws IOException {
		return NbtIo.readCompressed(new ByteArrayInputStream(bytes), NbtSizeTracker.ofUnlimitedBytes()).getListOrEmpty("i").stream()
				.map(NbtCompound.class::cast)
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

	@SuppressWarnings("deprecation")
	@NotNull
	public static NbtCompound getCustomData(ComponentHolder stack) {
		return stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).getNbt();
	}

	@NotNull
	public static String getId(ComponentHolder stack) {
		return getCustomData(stack).getString("id", "");
	}

	public static WrapperLookup getRegistryLookup() {
		return CLIENT.getNetworkHandler() != null && CLIENT.getNetworkHandler().getRegistryManager() != null ? CLIENT.getNetworkHandler().getRegistryManager() : DEFAULT_LOOKUP;
	}
}

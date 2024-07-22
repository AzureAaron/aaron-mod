package net.azureaaron.mod.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.text.Text;

public class ItemUtils {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final WrapperLookup DEFAULT_LOOKUP = BuiltinRegistries.createWrapperLookup();
	private static final int FIRST_DATA_VERSION = 100;

	private static boolean shouldLog = FabricLoader.getInstance().isDevelopmentEnvironment();

	public static List<ItemStack> parseCompressedItemData(String encoded) throws IOException {
		List<ItemStack> items = NbtIo.readCompressed(new ByteArrayInputStream(Base64.getDecoder().decode(encoded)), NbtSizeTracker.ofUnlimitedBytes()).getList("i", NbtElement.COMPOUND_TYPE).stream()
				.map(NbtCompound.class::cast)
				.map(ItemUtils::parseLegacyStack)
				.toList();

		return items;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack parseLegacyStack(NbtCompound nbt) {
		Dynamic<NbtElement> fixed = Schemas.getFixer().update(TypeReferences.ITEM_STACK, new Dynamic<>(getRegistryLookup().getOps(NbtOps.INSTANCE), nbt), FIRST_DATA_VERSION, SharedConstants.getGameVersion().getSaveVersion().getId());
		ItemStack stack = ItemStack.CODEC.parse(fixed)
				.setPartial(ItemStack.EMPTY)
				.resultOrPartial(ItemUtils::tryLogFixerError)
				.get();

		//Convert Name & Lore from legacy format
		if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
			stack.set(DataComponentTypes.CUSTOM_NAME, TextTransformer.fromLegacy(stack.get(DataComponentTypes.CUSTOM_NAME).getString()));
		}

		if (stack.contains(DataComponentTypes.LORE)) {
			List<Text> fixedLore = stack.get(DataComponentTypes.LORE).lines().stream()
					.map(Text::getString)
					.map(TextTransformer::fromLegacy)
					.map(Text.class::cast)
					.toList();

			stack.set(DataComponentTypes.LORE, new LoreComponent(fixedLore));
		}

		//Correct Custom Data
		if (stack.contains(DataComponentTypes.CUSTOM_DATA)) {
			stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(stack.get(DataComponentTypes.CUSTOM_DATA).getNbt().getCompound("ExtraAttributes")));
		}

		//Hide Vanilla Attributes
		stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT.withShowInTooltip(false));

		//Hide Vanilla Enchantments
		stack.set(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT.withShowInTooltip(false));

		//Always Display Skyblock Stuff on the item
		stack.setAlwaysDisplaySkyblockInfo(true);

		return stack;
	}

	public static WrapperLookup getRegistryLookup() {
		return CLIENT.getNetworkHandler() != null && CLIENT.getNetworkHandler().getRegistryManager() != null ? CLIENT.getNetworkHandler().getRegistryManager() : DEFAULT_LOOKUP;
	}

	private static void tryLogFixerError(String message) {
		if (shouldLog) {
			LOGGER.error("[Aaron's Mod] Failed to parse legacy item! Reason: {}", message);
		}
	}
}

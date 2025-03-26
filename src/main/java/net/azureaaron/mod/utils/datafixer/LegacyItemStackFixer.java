package net.azureaaron.mod.utils.datafixer;

import static net.azureaaron.legacyitemdfu.LegacyItemStackFixer.getFirstVersion;
import static net.azureaaron.legacyitemdfu.LegacyItemStackFixer.getFixer;
import static net.azureaaron.legacyitemdfu.LegacyItemStackFixer.getLatestVersion;

import java.util.List;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;

import net.azureaaron.legacyitemdfu.TypeReferences;
import net.azureaaron.mod.utils.ItemUtils;
import net.azureaaron.mod.utils.TextTransformer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;

public class LegacyItemStackFixer {
	private static final Logger LOGGER = LogUtils.getLogger();

	/**
	 * @deprecated Logging will eventually be always enabled.
	 */
	@Deprecated(forRemoval = true)
	private static boolean shouldLog = FabricLoader.getInstance().isDevelopmentEnvironment();

	@SuppressWarnings("deprecation")
	public static ItemStack fixLegacyStack(NbtCompound nbt) {
		if (nbt.getInt("id", 0) == 0) return ItemStack.EMPTY;

		Dynamic<NbtElement> fixed = getFixer().update(TypeReferences.LEGACY_ITEM_STACK, new Dynamic<>(ItemUtils.getRegistryLookup().getOps(NbtOps.INSTANCE), nbt), getFirstVersion(), getLatestVersion());
		ItemStack stack = ItemStack.CODEC.parse(fixed)
				.setPartial(ItemStack.EMPTY)
				.resultOrPartial(LegacyItemStackFixer::tryLogFixerError)
				.get();

		//Convert Custom Name & Lore to text components
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

		//Remap Custom Data
		if (stack.contains(DataComponentTypes.CUSTOM_DATA)) {
			stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(stack.get(DataComponentTypes.CUSTOM_DATA).getNbt().getCompoundOrEmpty("ExtraAttributes")));
		}

		//Hide Attributes & Vanilla Enchantments		
		TooltipDisplayComponent display = stack.getOrDefault(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT)
				.with(DataComponentTypes.ATTRIBUTE_MODIFIERS, true)
				.with(DataComponentTypes.ENCHANTMENTS, true);
		stack.set(DataComponentTypes.TOOLTIP_DISPLAY, display);

		//Always Display Skyblock Stuff on the item
		stack.setAlwaysDisplaySkyblockInfo(true);

		return stack;
	}

	private static void tryLogFixerError(String message) {
		if (shouldLog) {
			LOGGER.error("[Aaron's Mod Legacy Item Fixer] Failed to fix up legacy item! Reason: {}", message);
		}
	}
}

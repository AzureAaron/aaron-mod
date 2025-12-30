package net.azureaaron.mod.mixins;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> implements RecipeUpdateListener {
	@Unique
	private static final Minecraft CLIENT = Minecraft.getInstance();

	protected InventoryScreenMixin(InventoryMenu handler, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title) {
		super(handler, recipeBook, inventory, title);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void aaronMod$initDisplayGroups(Player player, CallbackInfo ci) {
		if (CLIENT.gameMode.getPlayerMode() != GameType.CREATIVE && AaronModConfigManager.get().refinements.tooltips.showItemGroupsOutsideCreative) {
			updateDisplayGroups(CLIENT.getConnection().searchTrees(), CLIENT.getConnection().enabledFeatures(), shouldShowOperatorTab(player), player.registryAccess());
		}
	}

	@Inject(method = "containerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;containerTick()V", shift = At.Shift.AFTER))
	private void aaronMod$updateDisplayGroups(CallbackInfo ci) {
		if (CLIENT.gameMode.getPlayerMode() != GameType.CREATIVE && CLIENT.player != null && AaronModConfigManager.get().refinements.tooltips.showItemGroupsOutsideCreative) {
			updateDisplayGroups(CLIENT.getConnection().searchTrees(), CLIENT.getConnection().enabledFeatures(), shouldShowOperatorTab(CLIENT.player), CLIENT.player.registryAccess());
		}
	}

	@Unique
	private boolean shouldShowOperatorTab(Player player) {
		return player.canUseGameMasterBlocks() && CLIENT.options.operatorItemsTab().get();
	}

	@Unique
	private boolean updateDisplayGroups(@Nullable SessionSearchTrees searchManager, FeatureFlagSet enabledFeatures, boolean showOperatorTab, Provider registries) {
		if (!CreativeModeTabs.tryRebuildTabContents(enabledFeatures, showOperatorTab, registries)) {
			return false;
		} else {
			if (searchManager != null) {
				List<ItemStack> list = List.copyOf(CreativeModeTabs.searchTab().getDisplayItems());

				searchManager.updateCreativeTooltips(registries, list);
				searchManager.updateCreativeTags(list);
			}

			return true;
		}
	}

	@Override
	public List<Component> getTooltipFromContainerItem(ItemStack stack) {
		List<Component> tooltip = super.getTooltipFromContainerItem(stack);

		if (AaronModConfigManager.get().refinements.tooltips.showItemGroupsOutsideCreative && !stack.has(DataComponents.CUSTOM_NAME) && !stack.isEnchanted()) {
			int count = 1;

			for (CreativeModeTab group : CreativeModeTabs.tabs()) {
				if (group.getType() != CreativeModeTab.Type.SEARCH && group.contains(stack)) {
					tooltip.add(count++, group.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
				}
			}
		}
		return tooltip;
	}
}

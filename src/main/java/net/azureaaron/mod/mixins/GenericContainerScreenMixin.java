package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Functions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

@Mixin(ContainerScreen.class)
public abstract class GenericContainerScreenMixin extends AbstractContainerScreen<ChestMenu>
implements MenuAccess<ChestMenu> {

	public GenericContainerScreenMixin(ChestMenu handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/ContainerScreen;renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V", ordinal = 0))
	private boolean aaronMod$hideScreenToolips(ContainerScreen container, GuiGraphics context, int mouseX, int mouseY) {
		return !(Functions.isOnHypixel() && AaronModConfigManager.get().skyblock.dungeons.hideClickOnTimeTooltips && this.title.getString().equals("Click the button on time!"));
	}
}

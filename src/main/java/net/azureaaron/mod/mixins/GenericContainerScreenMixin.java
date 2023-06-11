package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.features.MouseGuiPositioner;
import net.azureaaron.mod.util.Functions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;

@Mixin(GenericContainerScreen.class)
public abstract class GenericContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler> 
implements ScreenHandlerProvider<GenericContainerScreenHandler> {

	public GenericContainerScreenMixin(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/GenericContainerScreen;drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V", ordinal = 0))
	private void aaronMod$hideScreenToolips(GenericContainerScreen container, DrawContext context, int mouseX, int mouseY) {
		if(!(Functions.isOnHypixel() && Config.hideClickOnTimeTooltips && this.title.getString().equals("Click the button on time!"))) this.drawMouseoverTooltip(context, mouseX, mouseY);
	}
	
	@Override
	public boolean shouldCloseOnEsc() {
		MinecraftClient client = MinecraftClient.getInstance();
		((MouseGuiPositioner) client.mouse).reset();
		return true;
	}
}

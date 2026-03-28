package net.azureaaron.mod.mixins;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Functions;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {

	protected AbstractContainerScreenMixin(Component title) {
		super(title);
	}

	@WrapWithCondition(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractTooltip(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V", ordinal = 0))
	private boolean aaronMod$hideScreenToolips(AbstractContainerScreen<?> container, GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		return !(Functions.isOnHypixel() && AaronModConfigManager.get().skyblock.dungeons.hideClickOnTimeTooltips && this.title.getString().equals("Click the button on time!"));
	}

	@Inject(method = "keyPressed", at = @At("HEAD"))
	private void aaronMod$onKeyPress(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
		if (this.minecraft.options.keyInventory.matches(input) || (input.key() == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc())) {
			this.minecraft.mouseHandler.resetMousePos();
		}
	}
}

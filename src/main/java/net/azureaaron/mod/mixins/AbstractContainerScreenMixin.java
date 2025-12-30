package net.azureaaron.mod.mixins;

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

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {

	protected AbstractContainerScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "keyPressed", at = @At("HEAD"))
	private void aaronMod$onKeyPress(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
		if (this.minecraft.options.keyInventory.matches(input) || (input.key() == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc())) {
			this.minecraft.mouseHandler.resetMousePos();
		}
	}
}

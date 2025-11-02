package net.azureaaron.mod.mixins;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {

	protected HandledScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "keyPressed", at = @At("HEAD"))
	private void aaronMod$onKeyPress(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
		if (this.client.options.inventoryKey.matchesKey(input) || (input.key() == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc())) {
			this.client.mouse.resetMousePos();
		}
	}
}

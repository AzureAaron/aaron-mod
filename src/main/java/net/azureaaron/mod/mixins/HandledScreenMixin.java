package net.azureaaron.mod.mixins;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {

	protected HandledScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "keyPressed", at = @At("HEAD"))
	private void aaronMod$onKeyPress(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true, ordinal = 0) int keyCode, @Local(argsOnly = true, ordinal = 1) int scanCode) {
		if (client.options.inventoryKey.matchesKey(keyCode, scanCode) || (keyCode == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc())) {
			client.mouse.reset();
		}
	}
}

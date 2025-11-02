package net.azureaaron.mod.mixins;

import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.SeparateInventoryGuiScale;
import net.azureaaron.mod.features.SeparateInventoryGuiScale.SavedScaleState;
import net.azureaaron.mod.injected.MouseGuiPositioner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.util.Window;

@Mixin(Mouse.class)
public class MouseMixin implements MouseGuiPositioner {
	@Unique
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	@Shadow
	private double x;
	@Shadow
	private double y;
	@Unique
	private double guiX;
	@Unique
	private double guiY;

	@Inject(method = "lockCursor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;cursorLocked:Z", opcode = Opcodes.PUTFIELD))
	private void aaronMod$beforePositionLocked(CallbackInfo ci) {
		this.guiX = this.x;
		this.guiY = this.y;
	}

	@Inject(method = "unlockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(Lnet/minecraft/client/util/Window;IDD)V", shift = At.Shift.AFTER))
	private void aaronMod$afterUnlock(CallbackInfo ci) {
		if (AaronModConfigManager.get().refinements.input.dontResetCursorPosition && CLIENT.currentScreen instanceof GenericContainerScreen) {
			this.x = this.guiX;
			this.y = this.guiY;

			GLFW.glfwSetCursorPos(CLIENT.getWindow().getHandle(), this.x, this.y);
		}
	}

	@WrapMethod(method = { "scaleX(Lnet/minecraft/client/util/Window;D)D", "scaleY(Lnet/minecraft/client/util/Window;D)D" })
	private static double aaronMod$adjustPosition(Window window, double xOrY, Operation<Double> operation) {
		double scaled;

		if (SeparateInventoryGuiScale.isEnabled(CLIENT.currentScreen)) {
			SavedScaleState state = SavedScaleState.create(window).adjust();
			scaled = operation.call(window, xOrY);

			state.reset();
		} else {
			scaled = operation.call(window, xOrY);
		}

		return scaled;
	}

	@Override
	public void resetMousePos() {
		this.x = CLIENT.getWindow().getWidth() / 2;
		this.y = CLIENT.getWindow().getHeight() / 2;
	}
}

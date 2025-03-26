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
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.MouseInputEvent;
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
	@Shadow
	private double cursorDeltaX;
	@Shadow
	private double cursorDeltaY;

	@Unique
	private double guiX;
	@Unique
	private double guiY;

	@Inject(method = "onMouseButton", at = @At("HEAD"))
	private void aaronMod$onMouseButton(CallbackInfo ci, @Local(argsOnly = true, ordinal = 0) int button, @Local(argsOnly = true, ordinal = 1) int action, @Local(argsOnly = true, ordinal = 2) int mods) {
        MouseInputEvent.EVENT.invoker().onMouseInput(button, action, mods);
    }

	@Inject(method = "lockCursor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;x:D", ordinal = 0, shift = At.Shift.BEFORE))
	private void aaronMod$lockXPos(CallbackInfo ci) {
		this.guiX = this.x;
	}

	@Inject(method = "lockCursor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;y:D", ordinal = 0, shift = At.Shift.BEFORE))
	private void aaronMod$lockYPos(CallbackInfo ci) {
		this.guiY = this.y;		
	}

	@WrapOperation(method = "unlockCursor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;x:D", opcode = Opcodes.PUTFIELD, ordinal = 0))
	private void aaronMod$unlockXPos(Mouse mouse, double centreX, Operation<Void> operation) {
		if (AaronModConfigManager.get().refinements.input.dontResetCursorPosition && CLIENT.currentScreen instanceof GenericContainerScreen) {
			this.x = this.guiX;
		} else {
			operation.call(mouse, centreX);
		}
	}

	@WrapOperation(method = "unlockCursor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;y:D", opcode = Opcodes.PUTFIELD, ordinal = 0))
	private void aaronMod$unlockYPos(Mouse mouse, double centreY, Operation<Void> operation) {
		if (AaronModConfigManager.get().refinements.input.dontResetCursorPosition && CLIENT.currentScreen instanceof GenericContainerScreen) {
			this.y = this.guiY;
		} else {
			operation.call(mouse, centreY);
		}
	}

	@Inject(method = "unlockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(JIDD)V", ordinal = 0, shift = At.Shift.AFTER))
	private void aaronMod$correctCursorPosition(CallbackInfo ci) {
		if (AaronModConfigManager.get().refinements.input.dontResetCursorPosition && CLIENT.currentScreen instanceof GenericContainerScreen) GLFW.glfwSetCursorPos(CLIENT.getWindow().getHandle(), this.guiX, this.guiY);
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
	public void reset() {
		this.x = CLIENT.getWindow().getWidth() / 2;
		this.y = CLIENT.getWindow().getHeight() / 2;
	}
}

package net.azureaaron.mod.mixins;

import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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
		if (AaronModConfigManager.get().resetCursorPosition && CLIENT.currentScreen instanceof GenericContainerScreen) {
			this.x = this.guiX;
		} else {
			operation.call(mouse, centreX);
		}
	}

	@WrapOperation(method = "unlockCursor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;y:D", opcode = Opcodes.PUTFIELD, ordinal = 0))
	private void aaronMod$unlockYPos(Mouse mouse, double centreY, Operation<Void> operation) {
		if (AaronModConfigManager.get().resetCursorPosition && CLIENT.currentScreen instanceof GenericContainerScreen) {
			this.y = this.guiY;
		} else {
			operation.call(mouse, centreY);
		}
	}

	@Inject(method = "unlockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(JIDD)V", ordinal = 0, shift = At.Shift.AFTER))
	private void aaronMod$correctCursorPosition(CallbackInfo ci) {
		if (AaronModConfigManager.get().resetCursorPosition && CLIENT.currentScreen instanceof GenericContainerScreen) GLFW.glfwSetCursorPos(CLIENT.getWindow().getHandle(), this.guiX, this.guiY);
	}

	//Lambda in onMouseButton
	@ModifyArgs(method = "method_1611", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(DDI)Z"))
	private static void aaronMod$adjustMouseForSeparateScreenScalingOnClick(Args args) {
		adjustPosition(args, CLIENT.mouse.getX(), CLIENT.mouse.getY(), 0, 1);
	}

	//Lambda in onMouseButton
	@ModifyArgs(method = "method_1605", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(DDI)Z"))
	private static void aaronMod$adjustMouseForSeparateScreenScalingOnRelease(Args args) {
		adjustPosition(args, CLIENT.mouse.getX(), CLIENT.mouse.getY(), 0, 1);
	}

	@ModifyArgs(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDDD)Z"))
	private void aaronMod$adjustMouseForSeparateScreenScalingOnScroll(Args args) {
		adjustPosition(args, CLIENT.mouse.getX(), CLIENT.mouse.getY(), 0, 1);
	}

	//Lambda in tick
	@ModifyArgs(method = "method_55794", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseMoved(DD)V"))
	private static void aaronMod$adjustMouseForSeparateScreenScalingOnMove(Args args) {
		adjustPosition(args, CLIENT.mouse.getX(), CLIENT.mouse.getY(), 0, 1);
	}

	//Lambda in tick
	@ModifyArgs(method = "method_55795", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseDragged(DDIDD)Z"))
	private void aaronMod$adjustMouseForSeparateScreelScalingOnDrag(Args args) {
		adjustPosition(args, CLIENT.mouse.getX(), CLIENT.mouse.getY(), 0, 1);
		adjustPosition(args, this.cursorDeltaX, this.cursorDeltaY, 3, 4);
	}

	/**
	 * @param x Either the mouse's x position or the cursor delta x
	 * @param y Either the mouse's y position or the cursor delta y
	 */
	@Unique
	private static void adjustPosition(Args args, double x, double y, int xIndex, int yIndex) {
		if (SeparateInventoryGuiScale.isEnabled(CLIENT.currentScreen)) {
			Window window = CLIENT.getWindow();
			SavedScaleState state = SavedScaleState.create(window).adjust();

			double newX = x * (double) window.getScaledWidth() / (double) window.getWidth();
			double newY = y * (double) window.getScaledHeight() / (double) window.getHeight();

			args.set(xIndex, newX);
			args.set(yIndex, newY);

			state.reset();
		}
	}

	@Override
	public void reset() {
		this.x = CLIENT.getWindow().getWidth() / 2;
		this.y = CLIENT.getWindow().getHeight() / 2;
	}
}

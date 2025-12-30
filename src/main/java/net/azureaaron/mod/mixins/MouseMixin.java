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
import com.mojang.blaze3d.platform.Window;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.SeparateInventoryGuiScale;
import net.azureaaron.mod.features.SeparateInventoryGuiScale.SavedScaleState;
import net.azureaaron.mod.injected.MouseGuiPositioner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;

@Mixin(MouseHandler.class)
public class MouseMixin implements MouseGuiPositioner {
	@Unique
	private static final Minecraft CLIENT = Minecraft.getInstance();

	@Shadow
	private double xpos;
	@Shadow
	private double ypos;
	@Unique
	private double guiX;
	@Unique
	private double guiY;

	@Inject(method = "grabMouse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;mouseGrabbed:Z", opcode = Opcodes.PUTFIELD))
	private void aaronMod$beforePositionLocked(CallbackInfo ci) {
		this.guiX = this.xpos;
		this.guiY = this.ypos;
	}

	@Inject(method = "releaseMouse", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;grabOrReleaseMouse(Lcom/mojang/blaze3d/platform/Window;IDD)V", shift = At.Shift.AFTER))
	private void aaronMod$afterUnlock(CallbackInfo ci) {
		if (AaronModConfigManager.get().refinements.input.dontResetCursorPosition && CLIENT.screen instanceof ContainerScreen) {
			this.xpos = this.guiX;
			this.ypos = this.guiY;

			GLFW.glfwSetCursorPos(CLIENT.getWindow().handle(), this.xpos, this.ypos);
		}
	}

	@WrapMethod(method = { "getScaledXPos(Lcom/mojang/blaze3d/platform/Window;D)D", "getScaledYPos(Lcom/mojang/blaze3d/platform/Window;D)D" })
	private static double aaronMod$adjustPosition(Window window, double xOrY, Operation<Double> operation) {
		double scaled;

		if (SeparateInventoryGuiScale.isEnabled(CLIENT.screen)) {
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
		this.xpos = CLIENT.getWindow().getScreenWidth() / 2;
		this.ypos = CLIENT.getWindow().getScreenHeight() / 2;
	}
}

package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Window;

import net.azureaaron.mod.features.SeparateInventoryGuiScale;
import net.azureaaron.mod.features.SeparateInventoryGuiScale.SavedScaleState;
import net.azureaaron.mod.mixins.accessors.GameRenderStateAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;

@Mixin(Gui.class)
public class GuiMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	@Shadow
	@Final
	private GuiRenderState guiRenderState;

	// Inject after Fabric so that our handler also wraps the Screen API extract events
	@WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"), order = 1100)
	private void aaronMod$separateGuiScaleForScreenExtraction(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> operation) {
		if (SeparateInventoryGuiScale.isEnabled(screen)) {
			Window window = this.minecraft.getWindow();
			GuiRenderState screenGuiRenderState = new GuiRenderState();
			GameRenderState gameRenderState = this.minecraft.gameRenderer.gameRenderState();
			SavedScaleState scaleState = SavedScaleState.create(window).adjust();
			GuiRenderState oldGuiRenderState = this.guiRenderState;

			if (!screen.wasResized()) {
				screen.resize(window.getGuiScaledWidth(), window.getGuiScaledHeight());
				screen.markResized(true);
			}

			int newMouseX = (int) this.minecraft.mouseHandler.getScaledXPos(window);
			int newMouseY = (int) this.minecraft.mouseHandler.getScaledYPos(window);
			GuiGraphicsExtractor newGraphics = new GuiGraphicsExtractor(this.minecraft, screenGuiRenderState, newMouseX, newMouseY);

			// Setup render states with updated values
			((GameRenderStateAccessor) gameRenderState).setGuiRenderState(screenGuiRenderState);
			screenGuiRenderState.reset();
			gameRenderState.windowRenderState.guiScale = window.getGuiScale();

			// Extract the screen
			operation.call(screen, newGraphics, newMouseX, newMouseY, a);
			gameRenderState.setData(SeparateInventoryGuiScale.SEPARATED_GUI_SCALE_THIS_FRAME, true);

			// Reset state to what it was before
			scaleState.reset();
			((GameRenderStateAccessor) gameRenderState).setGuiRenderState(oldGuiRenderState);
			gameRenderState.windowRenderState.guiScale = window.getGuiScale();

			// Attach screen gui render state
			gameRenderState.setData(SeparateInventoryGuiScale.SCREEN_GUI_RENDER_STATE, screenGuiRenderState);
		} else {
			operation.call(screen, graphics, mouseX, mouseY, a);
		}
	}
}

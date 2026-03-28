package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Window;

import net.azureaaron.mod.features.SeparateInventoryGuiScale;
import net.azureaaron.mod.features.SeparateInventoryGuiScale.SavedScaleState;
import net.azureaaron.mod.mixins.accessors.GameRenderStateAccessor;
import net.azureaaron.mod.mixins.accessors.GuiRendererAccessor;
import net.azureaaron.mod.utils.render.GlowRenderer;
import net.azureaaron.mod.utils.render.GuiHelper;
import net.azureaaron.mod.utils.render.Renderer;
import net.azureaaron.mod.utils.render.ShaderUniforms;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.FogRenderer.FogMode;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;

@Mixin(value = GameRenderer.class)
public class GameRendererMixin {
	@Unique
	private static final RenderStateDataKey<Boolean> SEPARATED_GUI_SCALE_THIS_FRAME = RenderStateDataKey.create(() -> "Aaron Mod separated gui scale");
	@Shadow
	@Final
	private Minecraft minecraft;
	@Shadow
	@Final
	private FogRenderer fogRenderer;
	@Shadow
	@Final
	private GuiRenderer guiRenderer;
	@Shadow
	@Final
	private GameRenderState gameRenderState;
	@Unique
	private final GuiRenderState screenGuiRenderState = new GuiRenderState();

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlobalSettingsUniform;update(IIDJLnet/minecraft/client/DeltaTracker;ILnet/minecraft/world/phys/Vec3;Z)V", shift = At.Shift.AFTER))
	private void aaronMod$updateShaderUniforms(CallbackInfo ci, @Local(argsOnly = true) DeltaTracker tickCounter) {
		ShaderUniforms.updateShaderUniforms(tickCounter);
	}

	@Inject(method = "render", at = @At(value = "CONSTANT", args = "stringValue=gui"))
	private void skyblocker$onRenderGui(CallbackInfo ci) {
		GuiHelper.updateScreenBlitTexture();
	}

	@Inject(method = "close", at = @At("TAIL"))
	private void aaronMod$closeResources(CallbackInfo ci) {
		ShaderUniforms.close();
		Renderer.close();
		GlowRenderer.getInstance().close();
		GuiHelper.close();
	}

	// Inject after Fabric so that our handler also wraps the Screen API extract events
	@WrapOperation(method = "extractGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"), order = 1100)
	private void aaronMod$separateGuiScaleForScreenExtraction(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> operation) {
		if (SeparateInventoryGuiScale.isEnabled(screen)) {
			Window window = this.minecraft.getWindow();
			SavedScaleState scaleState = SavedScaleState.create(window).adjust();
			GuiRenderState oldGuiRenderState = this.gameRenderState.guiRenderState;

			if (!screen.wasResized()) {
				screen.resize(window.getGuiScaledWidth(), window.getGuiScaledHeight());
				screen.markResized(true);
			}

			int newMouseX = (int) this.minecraft.mouseHandler.getScaledXPos(window);
			int newMouseY = (int) this.minecraft.mouseHandler.getScaledYPos(window);
			GuiGraphicsExtractor newGraphics = new GuiGraphicsExtractor(this.minecraft, this.screenGuiRenderState, newMouseX, newMouseY);

			// Setup render states with updated values
			((GameRenderStateAccessor) this.gameRenderState).setGuiRenderState(this.screenGuiRenderState);
			this.screenGuiRenderState.reset();
			this.gameRenderState.windowRenderState.guiScale = window.getGuiScale();

			// Extract the screen
			operation.call(screen, newGraphics, newMouseX, newMouseY, a);
			this.gameRenderState.setData(SEPARATED_GUI_SCALE_THIS_FRAME, true);

			// Reset state to what it was before
			scaleState.reset();
			((GameRenderStateAccessor) this.gameRenderState).setGuiRenderState(oldGuiRenderState);
			this.gameRenderState.windowRenderState.guiScale = window.getGuiScale();
		} else {
			operation.call(screen, graphics, mouseX, mouseY, a);
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", shift = At.Shift.AFTER))
	private void aaronMod$separateGuiScaleForScreenRendering(CallbackInfo ci) {
		if (this.gameRenderState.getDataOrDefault(SEPARATED_GUI_SCALE_THIS_FRAME, false)) {
			Window window = this.minecraft.getWindow();
			SavedScaleState scaleState = SavedScaleState.create(window).adjust();

			// Setup state
			this.gameRenderState.windowRenderState.guiScale = window.getGuiScale();

			// Render screen
			((GuiRendererAccessor) this.guiRenderer).setRenderState(this.screenGuiRenderState);
			this.guiRenderer.render(this.fogRenderer.getBuffer(FogMode.NONE));
			((GuiRendererAccessor) this.guiRenderer).setRenderState(this.gameRenderState.guiRenderState);

			// Reset state
			scaleState.reset();
			this.gameRenderState.windowRenderState.guiScale = window.getGuiScale();
		}
	}
}

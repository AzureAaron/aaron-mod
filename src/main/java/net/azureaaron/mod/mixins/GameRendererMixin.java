package net.azureaaron.mod.mixins;

import java.util.Objects;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Window;

import net.azureaaron.mod.features.SeparateInventoryGuiScale;
import net.azureaaron.mod.features.SeparateInventoryGuiScale.SavedScaleState;
import net.azureaaron.mod.mixins.accessors.GuiRendererAccessor;
import net.azureaaron.mod.utils.render.GlowRenderer;
import net.azureaaron.mod.utils.render.GuiHelper;
import net.azureaaron.mod.utils.render.Renderer;
import net.azureaaron.mod.utils.render.ShaderUniforms;
import net.azureaaron.mod.utils.render.primitive.FilledBoxInstancedRenderer;
import net.azureaaron.mod.utils.render.primitive.OutlinedBoxInstancedRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.GameRenderState;

@Mixin(value = GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	@Shadow
	@Final
	private GuiRenderer guiRenderer;
	@Shadow
	@Final
	private GameRenderState gameRenderState;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlobalSettingsUniform;update(IIDJLnet/minecraft/client/DeltaTracker;ILnet/minecraft/world/phys/Vec3;Z)V", shift = At.Shift.AFTER))
	private void aaronMod$updateShaderUniforms(CallbackInfo ci, @Local(name = "deltaTracker") DeltaTracker deltaTracker) {
		ShaderUniforms.updateShaderUniforms(deltaTracker);
	}

	@Inject(method = "render", at = @At(value = "CONSTANT", args = "stringValue=gui"))
	private void skyblocker$onRenderGui(CallbackInfo ci) {
		GuiHelper.updateScreenBlitTexture();
	}

	@Inject(method = "close", at = @At("TAIL"))
	private void aaronMod$closeResources(CallbackInfo ci) {
		ShaderUniforms.close();
		FilledBoxInstancedRenderer.INSTANCE.close();
		OutlinedBoxInstancedRenderer.INSTANCE.close();
		Renderer.close();
		GlowRenderer.INSTANCE.close();
		GuiHelper.close();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render()V", shift = At.Shift.AFTER))
	private void aaronMod$separateGuiScaleForScreenRendering(CallbackInfo ci) {
		if (this.gameRenderState.getDataOrDefault(SeparateInventoryGuiScale.SEPARATED_GUI_SCALE_THIS_FRAME, false)) {
			Window window = this.minecraft.getWindow();
			SavedScaleState scaleState = SavedScaleState.create(window).adjust();

			// Setup state
			this.gameRenderState.windowRenderState.guiScale = window.getGuiScale();

			// Render screen
			((GuiRendererAccessor) this.guiRenderer).setRenderState(Objects.requireNonNull(this.gameRenderState.getData(SeparateInventoryGuiScale.SCREEN_GUI_RENDER_STATE), "screen gui render state must not be null"));
			this.guiRenderer.render();
			((GuiRendererAccessor) this.guiRenderer).setRenderState(this.gameRenderState.guiRenderState);

			// Reset state
			scaleState.reset();
			this.gameRenderState.windowRenderState.guiScale = window.getGuiScale();
		}
	}
}

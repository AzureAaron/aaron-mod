package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Window;
import net.azureaaron.mod.Keybinds;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.SeparateInventoryGuiScale;
import net.azureaaron.mod.features.SeparateInventoryGuiScale.SavedScaleState;
import net.azureaaron.mod.utils.render.GlowRenderer;
import net.azureaaron.mod.utils.render.GuiHelper;
import net.azureaaron.mod.utils.render.Renderer;
import net.azureaaron.mod.utils.render.ShaderUniforms;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.FogRenderer.FogMode;

@Mixin(value = GameRenderer.class, priority = 1100) //Inject after Fabric so that our handler also wraps the Screen API render events
public class GameRendererMixin {
	@Shadow
	@Final
	Minecraft minecraft;
	@Shadow
	@Final
	private FogRenderer fogRenderer;
	@Shadow
	@Final
	private GuiRenderer guiRenderer;

	@Unique
	private boolean cameraSmoothed = false;

	@ModifyReturnValue(method = "getFov", at = @At("RETURN"))
	private float aaronMod$zoom(float fov) {
		if (Keybinds.zoomKeybind.isDown()) {
			if (!this.cameraSmoothed) {
				this.cameraSmoothed = true;
				this.minecraft.options.smoothCamera = true;
			}

			return (float) (fov * AaronModConfigManager.get().uiAndVisuals.world.zoomMultiplier);
		} else if (this.cameraSmoothed) {
			this.cameraSmoothed = false;
			this.minecraft.options.smoothCamera = false;
		}

		return fov;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlobalSettingsUniform;update(IIDJLnet/minecraft/client/DeltaTracker;ILnet/minecraft/client/Camera;Z)V", shift = At.Shift.AFTER))
	private void aaronMod$updateShaderUniforms(CallbackInfo ci, @Local(argsOnly = true) DeltaTracker tickCounter) {
		ShaderUniforms.updateShaderUniforms(tickCounter);
	}

	@Inject(method = "close", at = @At("TAIL"))
	private void aaronMod$closeResources(CallbackInfo ci) {
		ShaderUniforms.close();
		Renderer.close();
		GlowRenderer.getInstance().close();
		GuiHelper.close();
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
	private void aaronMod$separateGUIScaleForScreens(Screen screen, GuiGraphics context, int mouseX, int mouseY, float delta, Operation<Void> operation) {
		if (SeparateInventoryGuiScale.isEnabled(screen)) {
			//Draw all stuff beforehand so that we don't interfere with the positioning or anything
			//This must be done here since the projection matrix inside by this method indirectly (down the chain of calls it performs)
			this.guiRenderer.render(this.fogRenderer.getBuffer(FogMode.NONE));

			Window window = this.minecraft.getWindow();
			SavedScaleState state = SavedScaleState.create(window).adjust();

			if (!screen.wasResized()) {
				screen.resize(window.getGuiScaledWidth(), window.getGuiScaledHeight());
				screen.markResized(true);
			}

			int newMouseX = (int) this.minecraft.mouseHandler.getScaledXPos(window);
			int newMouseY = (int) this.minecraft.mouseHandler.getScaledYPos(window);

			//Render the screen, then draw everything that the screen drew (if we don't then things like tooltip positions get messed up)
			operation.call(screen, context, newMouseX, newMouseY, delta);

			this.guiRenderer.render(this.fogRenderer.getBuffer(FogMode.NONE));

			//Reset State
			state.reset();
		} else {
			operation.call(screen, context, mouseX, mouseY, delta);
		}
	}
}

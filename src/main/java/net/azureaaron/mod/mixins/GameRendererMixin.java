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
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.Keybinds;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.SeparateInventoryGuiScale;
import net.azureaaron.mod.features.SeparateInventoryGuiScale.SavedScaleState;
import net.azureaaron.mod.screens.itemmodel.CustomizeItemModelScreen;
import net.azureaaron.mod.injected.GuiDepthStateTracker;
import net.azureaaron.mod.utils.render.ShaderUniforms;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.render.fog.FogRenderer.FogType;
import net.minecraft.client.util.Window;

@Mixin(value = GameRenderer.class, priority = 1100) //Inject after Fabric so that our handler also wraps the Screen API render events
public class GameRendererMixin implements GuiDepthStateTracker {
	@Shadow
	@Final
	MinecraftClient client;
	@Shadow
	@Final
	private FogRenderer fogRenderer;
	@Shadow
	@Final
	private GuiRenderer guiRenderer;

	@Unique
	private boolean cameraSmoothed = false;
	@Unique
	private boolean shouldUseSavedGuiDepth = false;

	@ModifyReturnValue(method = "getFov", at = @At("RETURN"))
	private float aaronMod$zoom(float fov) {
		if (Keybinds.zoomKeybind.isPressed()) {
			if (!this.cameraSmoothed) {
				this.cameraSmoothed = true; 
				this.client.options.smoothCameraEnabled = true; 
			}

			return (float) (fov * AaronModConfigManager.get().uiAndVisuals.world.zoomMultiplier);
		} else if (this.cameraSmoothed) {
			this.cameraSmoothed = false;
			this.client.options.smoothCameraEnabled = false;
		}

		return fov;
	}

	@Inject(method = "method_68478", at = @At("TAIL"))
	private static void aaronMod$applyScissorToBlur(CallbackInfo ci, @Local(argsOnly = true) RenderPass renderPass) {
		if (MinecraftClient.getInstance().currentScreen instanceof CustomizeItemModelScreen) {
			renderPass.enableScissor(RenderSystem.SCISSOR_STATE);
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/GlobalSettings;set(IIDJLnet/minecraft/client/render/RenderTickCounter;I)V", shift = At.Shift.AFTER))
	private void aaronMod$updateShaderUniforms(CallbackInfo ci, @Local(argsOnly = true) RenderTickCounter tickCounter) {
		ShaderUniforms.updateShaderUniforms(tickCounter);
	}

	@Inject(method = "close", at = @At("TAIL"))
	private void aaronMod$closeResources(CallbackInfo ci) {
		ShaderUniforms.close();
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
	private void aaronMod$separateGUIScaleForScreens(Screen screen, DrawContext context, int mouseX, int mouseY, float delta, Operation<Void> operation) {
		if (SeparateInventoryGuiScale.isEnabled(screen)) {
			//Draw all stuff beforehand so that we don't interfere with the positioning or anything
			//This must be done here since the projection matrix inside by this method indirectly (down the chain of calls it performs)
			this.guiRenderer.render(this.fogRenderer.getFogBuffer(FogType.NONE));

			Window window = this.client.getWindow();
			SavedScaleState state = SavedScaleState.create(window).adjust();

			if (!screen.wasResized()) {
				screen.resize(this.client, window.getScaledWidth(), window.getScaledHeight());
				screen.markResized(true);
			}

			int newMouseX = (int) this.client.mouse.getScaledX(window);
			int newMouseY = (int) this.client.mouse.getScaledY(window);

			//Render the screen, then draw everything that the screen drew (if we don't then things like tooltip positions get messed up)
			operation.call(screen, context, newMouseX, newMouseY, delta);

			this.shouldUseSavedGuiDepth = true;
			this.guiRenderer.render(this.fogRenderer.getFogBuffer(FogType.NONE));
			this.shouldUseSavedGuiDepth = false;

			//Reset State
			state.reset();
		} else {
			operation.call(screen, context, mouseX, mouseY, delta);
		}
	}

	@Override
	public boolean shouldUseSavedGuiDepth() {
		return this.shouldUseSavedGuiDepth;
	}
}

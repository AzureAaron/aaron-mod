package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import net.azureaaron.mod.utils.render.GlowRenderer;
import net.azureaaron.mod.utils.render.GuiHelper;
import net.azureaaron.mod.utils.render.Renderer;
import net.azureaaron.mod.utils.render.ShaderUniforms;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;

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

	/*@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"))
	private void aaronMod$separateGUIScaleForScreens(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, Operation<Void> operation) {
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
			operation.call(screen, graphics, newMouseX, newMouseY, delta);

			this.guiRenderer.render(this.fogRenderer.getBuffer(FogMode.NONE));

			//Reset State
			state.reset();
		} else {
			operation.call(screen, graphics, mouseX, mouseY, delta);
		}
	}*/
}

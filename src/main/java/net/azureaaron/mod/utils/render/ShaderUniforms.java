package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Scheduler;
import net.minecraft.client.render.RenderTickCounter;

public class ShaderUniforms {
	/** 
	 * Used to create a custom core shader uniform named {@code Ticks} which is a singular {@code float} value.<br>
	 * 
	 * @implNote This custom uniform is similar to the {@code GameTime} uniform expect that the time is in<br>
	 *  relation to the current client tick and render tick progress which ensures that the uniform will work even when not in a world.<br>
	 */
	private static float shaderTicks;

	public static void updateShaderTicks(RenderTickCounter tickCounter) {
		RenderSystem.assertOnRenderThread();
		shaderTicks = Scheduler.INSTANCE.getCurrentTick() + tickCounter.getTickProgress(true);
	}

	public static float getShaderTicks() {
		RenderSystem.assertOnRenderThread();
		return shaderTicks;
	}

	public static float getShaderChromaSize() {
		RenderSystem.assertOnRenderThread();
		return AaronModConfigManager.get().uiAndVisuals.chromaText.chromaSize;
	}

	public static float getShaderChromaSpeed() {
		RenderSystem.assertOnRenderThread();
		return AaronModConfigManager.get().uiAndVisuals.chromaText.chromaSpeed;
	}

	public static float getShaderChromaSaturation() {
		RenderSystem.assertOnRenderThread();
		return AaronModConfigManager.get().uiAndVisuals.chromaText.chromaSaturation;
	}
}

package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.config.AaronModConfigManager;

public class ShaderUniforms {
	/** 
	 * Used to create a custom core shader uniform named {@code Time} which is a singular {@code float} value.<br>
	 * This uniform can be used by any core shader in the game!
	 * 
	 * @implNote This custom uniform is similar to the {@code GameTime} uniform expect that the time is in<br>
	 *  relation to the current unix timestamp which ensures that the uniform will work even when not in a world.<br>
	 */
	private static float shaderTime;

	public static void updateShaderTime() {
		RenderSystem.assertOnRenderThread();
		shaderTime = (System.currentTimeMillis() % 30000L) / 30000f;
	}

	public static float getShaderTime() {
		RenderSystem.assertOnRenderThread();

		return shaderTime;
	}

	public static float getShaderChromaSpeed() {
		return AaronModConfigManager.get().uiAndVisuals.chromaText.chromaSpeed;
	}

	public static float getShaderChromaSaturation() {
		return AaronModConfigManager.get().uiAndVisuals.chromaText.chromaSaturation;
	}
}

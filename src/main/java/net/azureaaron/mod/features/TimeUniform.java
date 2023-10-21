package net.azureaaron.mod.features;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.annotations.InterfaceInjected;
import net.minecraft.client.gl.GlUniform;

public class TimeUniform {
	/** 
	 * Used to create a custom core shader uniform named {@code Time} which is a singular {@code float} value.<br>
	 * This uniform can be used by any core shader in the game!
	 * 
	 * @implNote This custom uniform is similar to the {@code GameTime} uniform expect that the time is in<br>
	 *  relation to the current unix timestamp which ensures that the uniform will work even when not in a world.<br>
	 */
	private static float shaderTime;
	
	public static void updateShaderTime() {
		float time = (System.currentTimeMillis() % 30000L) / 30000f;
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> {
				shaderTime = time;
			});
		} else {
			shaderTime = time;
		}
	}
	
	public static float getShaderTime() {
		RenderSystem.assertOnRenderThread();
		
		return shaderTime;
	}
	
	@InterfaceInjected
	public interface Getter {
		GlUniform getTime();
	}
}

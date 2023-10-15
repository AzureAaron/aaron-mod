package net.azureaaron.mod.features;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.features.BoundingBoxes.Dragons;
import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.util.math.Vec3d;

public class M7Waypoints {
	private static final float ALPHA = 0.5f;
	
	// Shoot Spots
	private static final Vec3d BLUE_SHOOT = new Vec3d(85, 20, 94);
	private static final Vec3d GREEN_SHOOT = new Vec3d(27, 20, 94);
	private static final Vec3d ORANGE_SHOOT = new Vec3d(84, 20, 56);
	private static final Vec3d PURPLE_SHOOT = new Vec3d(56, 20, 124);
	private static final Vec3d RED_SHOOT = new Vec3d(27, 20, 59);
	
	//Stack Spots
	private static final Vec3d ORANGE_STACK = new Vec3d(57, 4, 78);
	private static final Vec3d BLUE_STACK = new Vec3d(57, 4, 78);
	private static final Vec3d BLUE_STACK_ALT = new Vec3d(61, 6, 118);
	private static final Vec3d PURPLE_STACK = new Vec3d(27, 5, 94);
	private static final Vec3d PURPLE_STACK_ALT = new Vec3d(86, 5, 96);
	private static final Vec3d GREEN_STACK = new Vec3d(55, 6, 116);
	private static final Vec3d GREEN_STACK_ALT = new Vec3d(28, 5, 44);
	private static final Vec3d RED_STACK = new Vec3d(20, 5, 86);
	private static final Vec3d RED_STACK_ALT = new Vec3d(12, 6, 90);
	
	public static void renderWaypoints(WorldRenderContext wrc) {
		if (Functions.isOnHypixel() && Cache.inM7Phase5) {
			/*if (Config.m7GyroWaypoints) {
				renderFilled(wrc, BLUE_GYRO, 252f * 255f, 211f * 255f, 3 * 255f);
				renderFilled(wrc, GREEN_GYRO, 252f * 255f, 211f * 255f, 3 * 255f);
			}*/
			
			if (Config.m7ShootWaypoints) {
				Renderer.renderFilledBox(wrc, BLUE_SHOOT, Dragons.ICE.red, Dragons.ICE.green, Dragons.ICE.blue, ALPHA);
				Renderer.renderFilledBox(wrc, GREEN_SHOOT, Dragons.APEX.red, Dragons.APEX.green, Dragons.APEX.blue, ALPHA);
				Renderer.renderFilledBox(wrc, ORANGE_SHOOT, Dragons.FLAME.red, Dragons.FLAME.green, Dragons.FLAME.blue, ALPHA);
				Renderer.renderFilledBox(wrc, PURPLE_SHOOT, Dragons.SOUL.red, Dragons.SOUL.green, Dragons.SOUL.blue, ALPHA);
				Renderer.renderFilledBox(wrc, RED_SHOOT, Dragons.POWER.red, Dragons.POWER.green, Dragons.POWER.blue, ALPHA);
			}
			
			if (Config.m7StackWaypoints) {
				Renderer.renderHalfFilledBox(wrc, ORANGE_STACK, Dragons.FLAME.red, Dragons.FLAME.green, Dragons.FLAME.blue, ALPHA, false);
				Renderer.renderHalfFilledBox(wrc, BLUE_STACK, Dragons.ICE.red, Dragons.ICE.green, Dragons.ICE.blue, ALPHA, true);
				Renderer.renderFilledBox(wrc, BLUE_STACK_ALT, Dragons.ICE.red, Dragons.ICE.green, Dragons.ICE.blue, ALPHA);
				Renderer.renderFilledBox(wrc, PURPLE_STACK, Dragons.SOUL.red, Dragons.SOUL.green, Dragons.SOUL.blue, ALPHA);
				Renderer.renderFilledBox(wrc, PURPLE_STACK_ALT, Dragons.SOUL.red, Dragons.SOUL.green, Dragons.SOUL.blue, ALPHA);
				Renderer.renderFilledBox(wrc, GREEN_STACK, Dragons.APEX.red, Dragons.APEX.green, Dragons.APEX.blue, ALPHA);
				Renderer.renderFilledBox(wrc, GREEN_STACK_ALT, Dragons.APEX.red, Dragons.APEX.green, Dragons.APEX.blue, ALPHA);
				Renderer.renderFilledBox(wrc, RED_STACK, Dragons.POWER.red, Dragons.POWER.green, Dragons.POWER.blue, ALPHA);
				Renderer.renderFilledBox(wrc, RED_STACK_ALT, Dragons.POWER.red, Dragons.POWER.green, Dragons.POWER.blue, ALPHA);
			}
		}
	}
}

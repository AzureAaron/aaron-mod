package net.azureaaron.mod.features;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.render.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
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
	private static final Vec3d ORANGE_STACK = new Vec3d(48, 4, 80); // 45, 4, 82 or 48, 4, 80
	private static final Vec3d BLUE_STACK = new Vec3d(41, 5, 108); // 41, 5, 108
	private static final Vec3d PURPLE_STACK = new Vec3d(24, 5, 92); // 24, 5, 92
	private static final Vec3d PURPLE_STACK_ALT = new Vec3d(88, 5, 92); // 88, 5, 92
	private static final Vec3d GREEN_STACK = new Vec3d(62, 5, 109); // 66, 5, 109 or 62, 5, 109
	private static final Vec3d RED_STACK = new Vec3d(16, 5, 89); //16, 5, 89
	
	public static void init() {
		WorldRenderEvents.AFTER_TRANSLUCENT.register(M7Waypoints::renderWaypoints);
	}
	
	private static void renderWaypoints(WorldRenderContext wrc) {
		if (Functions.isOnHypixel() && Cache.inM7Phase5) {
			if (AaronModConfigManager.get().m7ShootWaypoints) {
				Renderer.renderFilledBox(wrc, BLUE_SHOOT, Dragons.ICE.red, Dragons.ICE.green, Dragons.ICE.blue, ALPHA);
				Renderer.renderFilledBox(wrc, GREEN_SHOOT, Dragons.APEX.red, Dragons.APEX.green, Dragons.APEX.blue, ALPHA);
				Renderer.renderFilledBox(wrc, ORANGE_SHOOT, Dragons.FLAME.red, Dragons.FLAME.green, Dragons.FLAME.blue, ALPHA);
				Renderer.renderFilledBox(wrc, PURPLE_SHOOT, Dragons.SOUL.red, Dragons.SOUL.green, Dragons.SOUL.blue, ALPHA);
				Renderer.renderFilledBox(wrc, RED_SHOOT, Dragons.POWER.red, Dragons.POWER.green, Dragons.POWER.blue, ALPHA);
			}
			
			if (AaronModConfigManager.get().m7StackWaypoints) {
				Renderer.renderFilledBox(wrc, ORANGE_STACK, Dragons.FLAME.red, Dragons.FLAME.green, Dragons.FLAME.blue, ALPHA);
				Renderer.renderFilledBox(wrc, BLUE_STACK, Dragons.ICE.red, Dragons.ICE.green, Dragons.ICE.blue, ALPHA);
				Renderer.renderFilledBox(wrc, PURPLE_STACK, Dragons.SOUL.red, Dragons.SOUL.green, Dragons.SOUL.blue, ALPHA);
				Renderer.renderFilledBox(wrc, PURPLE_STACK_ALT, Dragons.SOUL.red, Dragons.SOUL.green, Dragons.SOUL.blue, ALPHA);
				Renderer.renderFilledBox(wrc, GREEN_STACK, Dragons.APEX.red, Dragons.APEX.green, Dragons.APEX.blue, ALPHA);
				Renderer.renderFilledBox(wrc, RED_STACK, Dragons.POWER.red, Dragons.POWER.green, Dragons.POWER.blue, ALPHA);
			}
		}
	}
}

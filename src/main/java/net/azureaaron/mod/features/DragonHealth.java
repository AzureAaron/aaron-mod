package net.azureaaron.mod.features;

import java.awt.Color;
import java.util.List;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class DragonHealth {
	private static final int MAX_DRAGON_HP = 1_000_000_000;
	
	public static void init() {
		WorldRenderEvents.END.register(DragonHealth::render);
	}
	
	@SuppressWarnings("resource")
	private static void render(WorldRenderContext wrc) {
		try {
			if (Cache.inM7Phase5 && Config.m7DragonHealth) {
				ClientWorld world = MinecraftClient.getInstance().world;
				
				if (world != null) {
					for (Entity entity : world.getEntities()) {
						if (entity instanceof EnderDragonEntity dragon) {
							List<ArmorStandEntity> armourStands = world.getEntitiesByClass(ArmorStandEntity.class, dragon.getBoundingBox(), e -> e.hasCustomName());
							
							for (ArmorStandEntity armourStand : armourStands) {
								String name = armourStand.getName().getString();
								
								if (name.contains("Withered Dragon")) {
									String firstSegment = name.substring(0, name.indexOf('/'));
									String healthSegment = firstSegment.substring(firstSegment.lastIndexOf(' ') + 1).replaceAll(",", "");
									
									float health = getHealth(healthSegment);
									float hp = (health / MAX_DRAGON_HP) * 100f;
									
									int colour = getHealthColour(hp);
									Vec3d pos = new Vec3d(dragon.getX(), dragon.getY() - 1, dragon.getZ());
									
									Renderer.renderText(wrc, pos, Text.literal(Functions.NUMBER_FORMATTER_S.format(health)).styled(style -> style.withColor(colour)).asOrderedText(), true);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Main.LOGGER.error("[Aaron's Mod] Failed to render a dragon's health! {}", e);
		}
	}
	
	private static int getHealth(String health) {
		try {
			if (health.endsWith("B")) return Integer.parseInt(health.replace("B", "000000000"));
			if (health.endsWith("M")) return Integer.parseInt(health.replace("M", "000000"));
			if (health.endsWith("k")) return Integer.parseInt(health.replace("k", "000"));
			
			return Integer.parseInt(health);
		} catch (Exception e) {
			Main.LOGGER.error("[Aaron's Mod] Failed to parse dragon health! Input: {}, Exception: {}", health, e);
		}
		
		return 0;
	}
	
	private static int getHealthColour(float percentage) {
		return Color.HSBtoRGB(percentage / 300f, 0.9f, 0.9f);
	}
}

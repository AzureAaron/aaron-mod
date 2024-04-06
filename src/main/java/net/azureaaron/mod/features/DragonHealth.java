package net.azureaaron.mod.features;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.mixins.accessors.ClientEntityManagerAccessor;
import net.azureaaron.mod.mixins.accessors.ClientWorldAccessor;
import net.azureaaron.mod.util.Cache;
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
import net.minecraft.world.entity.EntityIndex;

public class DragonHealth {
	private static final Pattern DRAGON_HP = Pattern.compile("﴾ Withered Dragon (?:\u16E4 )?(?<health>[0-9kKMB.]+)\\/(?<max>[0-9kKMB.]+)\u2764 ﴿");
	
	public static void init() {
		WorldRenderEvents.END.register(DragonHealth::render);
	}
	
	@SuppressWarnings("resource")
	private static void render(WorldRenderContext wrc) {
		try {
			if (Cache.inM7Phase5 && AaronModConfigManager.get().m7DragonHealth) {
				ClientWorld world = MinecraftClient.getInstance().world;
				
				if (world != null) {
					for (Entity entity : world.getEntities()) {
						if (entity instanceof EnderDragonEntity dragon) {
							@SuppressWarnings("unchecked")
							EntityIndex<Entity> entityIndex = ((ClientEntityManagerAccessor<Entity>) ((ClientWorldAccessor) world).getEntityManager()).getIndex();

							for (Entity indexedEntity : entityIndex.iterate()) {
								if (indexedEntity instanceof ArmorStandEntity armourStand && armourStand.getBoundingBox().intersects(dragon.getBoundingBox())) {
									String name = armourStand.getName().getString();
									Matcher matcher = DRAGON_HP.matcher(name);

									if (matcher.matches()) {
										String healthSegment = matcher.group("health");
										String maxHealthSegment = matcher.group("max");
										
										float health = (float) getHealth(healthSegment);
										float maxHealth = (float) getHealth(maxHealthSegment);
										float hp = (health / maxHealth) * 100f;
										
										int colour = getHealthColour(hp);
										Vec3d pos = new Vec3d(dragon.getX(), dragon.getY() - 1, dragon.getZ());
										
										Renderer.renderText(wrc, pos, Text.literal(healthSegment).styled(style -> style.withColor(colour)).asOrderedText(), true);
										
										break;
									}
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
	
	private static double getHealth(String health) {
		try {
			double multiplier = 1;
			health = health.toUpperCase();
			
			if (health.endsWith("K")) {
				multiplier = 1e3;
				health = health.substring(0, health.length() - 1);
			} else if (health.endsWith("M")) {
				multiplier = 1e6;
				health = health.substring(0, health.length() - 1);
			} else if (health.endsWith("B")) {
				multiplier = 1e9;
				health = health.substring(0, health.length() - 1);
			}
			
			if (!health.contains(".")) {
				health += ".0";
			}
			
			return Double.parseDouble(health) * multiplier;
		} catch (Exception e) {
			Main.LOGGER.error("[Aaron's Mod] Failed to parse dragon health! Input: {}", health, e);
		}
		
		return 0d;
	}
	
	private static int getHealthColour(float percentage) {
		return Color.HSBtoRGB(percentage / 300f, 0.9f, 0.9f);
	}
}

package net.azureaaron.mod.features;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.WorldRenderExtractionCallback;
import net.azureaaron.mod.mixins.accessors.ClientEntityManagerAccessor;
import net.azureaaron.mod.mixins.accessors.ClientWorldAccessor;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.render.primitive.PrimitiveCollector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.entity.EntityIndex;

public class DragonHealth {
	//﴾ (?:.+ )?Withered Dragon (?:\u16E4 )?(?<health>[\dkKMB.]+)\/(?<max>[\dkKMB.]+)\u2764 ﴿
	private static final Pattern DRAGON_HP = Pattern.compile("﴾ (?:.+ )?Withered Dragon (?:\\u16E4 )?(?<health>[\\dkKMB.]+)\\/(?<max>[\\dkKMB.]+)\\u2764 ﴿");

	@Init
	public static void init() {
		WorldRenderExtractionCallback.EVENT.register(DragonHealth::extractRendering);
	}

	private static void extractRendering(PrimitiveCollector collector) {
		try {
			if (Cache.inM7Phase5 && AaronModConfigManager.get().skyblock.m7.dragonHealthDisplay) {
				MinecraftClient client = MinecraftClient.getInstance();
				ClientWorld world = client.world;

				if (world != null) {
					for (Entity entity : world.getEntities()) {
						if (entity instanceof EnderDragonEntity dragon) {
							@SuppressWarnings("unchecked")
							EntityIndex<Entity> entityIndex = ((ClientEntityManagerAccessor<Entity>) ((ClientWorldAccessor) world).getEntityManager()).getIndex();

							for (Entity indexedEntity : entityIndex.iterate()) {
								if (indexedEntity instanceof ArmorStandEntity armourStand && armourStand.getBoundingBox().intersects(dragon.getBoundingBox()) && armourStand.hasCustomName()) {
									String name = armourStand.getName().getString();
									Matcher matcher = DRAGON_HP.matcher(name);

									if (matcher.matches()) {
										String healthSegment = matcher.group("health");
										String maxHealthSegment = matcher.group("max");

										float health = (float) getHealth(healthSegment);
										float maxHealth = (float) getHealth(maxHealthSegment);
										float hp = (health / maxHealth) * 100f;

										int colour = getHealthColour(hp);
										Vec3d pos = dragon.getLerpedPos(client.getRenderTickCounter().getTickProgress(false)).subtract(0, 1, 0);

										collector.submitText(Text.literal(healthSegment).styled(style -> style.withColor(colour)), pos, 8, true);

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

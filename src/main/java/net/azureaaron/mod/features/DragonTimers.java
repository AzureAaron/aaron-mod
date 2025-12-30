package net.azureaaron.mod.features;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.ParticleSpawnEvent;
import net.azureaaron.mod.events.ServerTickCallback;
import net.azureaaron.mod.events.WorldRenderExtractionCallback;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.ServerTickCounter;
import net.azureaaron.mod.utils.render.primitive.PrimitiveCollector;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.util.Util;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DragonTimers {
	private static final Vec3 POWER_TEXT_LOCATION = new Vec3(26, 16, 59); //26 6 59
	private static final Vec3 FLAME_TEXT_LOCATION = new Vec3(86, 16, 56); //86 6 56
	private static final Vec3 APEX_TEXT_LOCATION = new Vec3(26, 16, 94); //26 6 94
	private static final Vec3 ICE_TEXT_LOCATION = new Vec3(85, 16, 94); //85 6 94
	private static final Vec3 SOUL_TEXT_LOCATION = new Vec3(56, 16, 126); //56 8 126

	private static final Reference2ObjectOpenHashMap<Dragons, Vec3> DRAGON_SPAWN_TEXT_LOCATIONS = Util.make(new Reference2ObjectOpenHashMap<>(), map -> {
		map.put(Dragons.POWER, POWER_TEXT_LOCATION);
		map.put(Dragons.FLAME, FLAME_TEXT_LOCATION);
		map.put(Dragons.APEX, APEX_TEXT_LOCATION);
		map.put(Dragons.ICE, ICE_TEXT_LOCATION);
		map.put(Dragons.SOUL, SOUL_TEXT_LOCATION);
	});

	@Init
	public static void init() {
		WorldRenderExtractionCallback.EVENT.register(DragonTimers::extractRendering);
		ParticleSpawnEvent.EVENT.register(DragonTimers::onParticle);
		ServerTickCallback.EVENT.register(DragonTimers::onServerTick);
	}

	private static void extractRendering(PrimitiveCollector collector) {
		if (Functions.isOnHypixel() && AaronModConfigManager.get().skyblock.m7.dragonSpawnTimers && Cache.inM7Phase5) {
			for (Dragons dragon : Dragons.VALUES) {
				if (dragon.spawnTime > 0) {
					int timeUntilSpawn = (int) (dragon.spawnTime * ServerTickCounter.MILLIS_PER_TICK);
					Component spawnText = Component.literal(timeUntilSpawn + " ms");

					collector.submitText(spawnText, DRAGON_SPAWN_TEXT_LOCATIONS.get(dragon), 8, true);
				}
			}
		}
	}

	private static void onParticle(ClientboundLevelParticlesPacket packet) {
		if (Functions.isOnHypixel() && Cache.inM7Phase5 && packet.getParticle().getType().equals(ParticleTypes.ENCHANT)) {
			for (Dragons dragon : Dragons.VALUES) {
				int xShrinkFactor = (dragon.pos1.getX() == 41) ? 11 : 0;
				int zShrinkFactor = (dragon.pos1.getZ() == 112) ? 0 : 11;
				AABB box = AABB.encapsulatingFullBlocks(dragon.pos1.offset(0, 14, 0), dragon.pos2).deflate(xShrinkFactor, 0, zShrinkFactor);

				if (box.contains(packet.getX(), packet.getY(), packet.getZ())) {
					if (dragon.spawnTime <= 0) {
						dragon.spawnTime = 5 * 20;
						DragonNotifications.notifySpawn(dragon);
					}
				}
			}
		}
	}

	private static void onServerTick() {
		if (Functions.isOnHypixel() && Cache.inM7Phase5) {
			for (Dragons dragon : Dragons.VALUES) {
				if (dragon.spawnTime > 0) {
					dragon.spawnTime--;
				}
			}
		}
	}
}

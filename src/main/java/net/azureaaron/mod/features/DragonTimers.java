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
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class DragonTimers {
	private static final Vec3d POWER_TEXT_LOCATION = new Vec3d(26, 16, 59); //26 6 59
	private static final Vec3d FLAME_TEXT_LOCATION = new Vec3d(86, 16, 56); //86 6 56
	private static final Vec3d APEX_TEXT_LOCATION = new Vec3d(26, 16, 94); //26 6 94
	private static final Vec3d ICE_TEXT_LOCATION = new Vec3d(85, 16, 94); //85 6 94
	private static final Vec3d SOUL_TEXT_LOCATION = new Vec3d(56, 16, 126); //56 8 126

	private static final Reference2ObjectOpenHashMap<Dragons, Vec3d> DRAGON_SPAWN_TEXT_LOCATIONS = Util.make(new Reference2ObjectOpenHashMap<>(), map -> {
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
					Text spawnText = Text.literal(timeUntilSpawn + " ms");

					collector.submitText(spawnText, DRAGON_SPAWN_TEXT_LOCATIONS.get(dragon), 8, true);
				}
			}
		}
	}

	private static void onParticle(ParticleS2CPacket packet) {
		if (Functions.isOnHypixel() && Cache.inM7Phase5 && packet.getParameters().getType().equals(ParticleTypes.ENCHANT)) {
			for (Dragons dragon : Dragons.VALUES) {
				int xShrinkFactor = (dragon.pos1.getX() == 41) ? 11 : 0;
				int zShrinkFactor = (dragon.pos1.getZ() == 112) ? 0 : 11;
				Box box = Box.enclosing(dragon.pos1.add(0, 14, 0), dragon.pos2).contract(xShrinkFactor, 0, zShrinkFactor);

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

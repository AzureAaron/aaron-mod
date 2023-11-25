package net.azureaaron.mod.features;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.events.ParticleSpawnEvent;
import net.azureaaron.mod.features.BoundingBoxes.Dragons;
import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class DragonTimers {
	private static final Vec3d POWER_TEXT_LOCATION = new Vec3d(26, 16, 59); //26 6 59
	private static final Vec3d FLAME_TEXT_LOCATION = new Vec3d(86, 16, 56); //86 6 56
	private static final Vec3d APEX_TEXT_LOCATION = new Vec3d(26, 16, 94); //26 6 94
	private static final Vec3d ICE_TEXT_LOCATION = new Vec3d(85, 16, 94); //85 6 94
	private static final Vec3d SOUL_TEXT_LOCATION = new Vec3d(56, 16, 126); //56 8 126
	
	public static void init() {
		WorldRenderEvents.BEFORE_DEBUG_RENDER.register(DragonTimers::renderSpawnTimers);
		ParticleSpawnEvent.EVENT.register(DragonTimers::tick);
	}
	
	private static void renderSpawnTimers(WorldRenderContext wrc) {
		if(Functions.isOnHypixel() && Config.m7DragonSpawnTimers && Cache.inM7Phase5) {
			if(Cache.powerSpawnStart != 0L && Cache.powerSpawnStart + 5000 > System.currentTimeMillis()) {
				int timeUntilSpawn = (int) (Cache.powerSpawnStart + 5000 - System.currentTimeMillis());
				OrderedText spawnText = Text.literal(timeUntilSpawn + " ms").asOrderedText();
				Renderer.renderText(wrc, POWER_TEXT_LOCATION, spawnText, true);
			}
			
			if(Cache.flameSpawnStart != 0L && Cache.flameSpawnStart + 5000 > System.currentTimeMillis()) {
				int timeUntilSpawn = (int) (Cache.flameSpawnStart + 5000 - System.currentTimeMillis());
				OrderedText spawnText = Text.literal(timeUntilSpawn + " ms").asOrderedText();
				Renderer.renderText(wrc, FLAME_TEXT_LOCATION, spawnText, true);
			}
			
			if(Cache.apexSpawnStart != 0L && Cache.apexSpawnStart + 5000 > System.currentTimeMillis()) {
				int timeUntilSpawn = (int) (Cache.apexSpawnStart + 5000 - System.currentTimeMillis());
				OrderedText spawnText = Text.literal(timeUntilSpawn + " ms").asOrderedText();
				Renderer.renderText(wrc, APEX_TEXT_LOCATION, spawnText, true);
			}
			
			if(Cache.iceSpawnStart != 0L && Cache.iceSpawnStart + 5000 > System.currentTimeMillis()) {
				int timeUntilSpawn = (int) (Cache.iceSpawnStart + 5000 - System.currentTimeMillis());
				OrderedText spawnText = Text.literal(timeUntilSpawn + " ms").asOrderedText();
				Renderer.renderText(wrc, ICE_TEXT_LOCATION, spawnText, true);
			}
			
			if(Cache.soulSpawnStart != 0L && Cache.soulSpawnStart + 5000 > System.currentTimeMillis()) {
				int timeUntilSpawn = (int) (Cache.soulSpawnStart + 5000 - System.currentTimeMillis());
				OrderedText spawnText = Text.literal(timeUntilSpawn + " ms").asOrderedText();
				Renderer.renderText(wrc, SOUL_TEXT_LOCATION, spawnText, true);
			}
		}
	}
	
	private static void tick(ParticleS2CPacket packet) {
		if(Functions.isOnHypixel() && Cache.inM7Phase5 && packet.getParameters().getType().equals(ParticleTypes.ENCHANT)) {
			for(Dragons dragon : Dragons.values()) {
				String dragonName = dragon.name();
				int xShrinkFactor = (dragon.pos1.getX() == 41) ? 11 : 0;
				int zShrinkFactor = (dragon.pos1.getZ() == 112) ? 0 : 11;
				Box box = Box.enclosing(dragon.pos1.add(0, 14, 0), dragon.pos2).contract(xShrinkFactor, 0, zShrinkFactor);
				
				if(box.contains(packet.getX(), packet.getY(), packet.getZ())) {
					if(dragonName.equals("POWER") && Cache.powerSpawnStart + 5000 < System.currentTimeMillis()) Cache.powerSpawnStart = System.currentTimeMillis();
					if(dragonName.equals("FLAME") && Cache.flameSpawnStart + 5000 < System.currentTimeMillis()) Cache.flameSpawnStart = System.currentTimeMillis();
					if(dragonName.equals("APEX") && Cache.apexSpawnStart + 5000 < System.currentTimeMillis()) Cache.apexSpawnStart = System.currentTimeMillis();
					if(dragonName.equals("ICE") && Cache.iceSpawnStart + 5000 < System.currentTimeMillis()) Cache.iceSpawnStart = System.currentTimeMillis();
					if(dragonName.equals("SOUL") && Cache.soulSpawnStart + 5000 < System.currentTimeMillis()) Cache.soulSpawnStart = System.currentTimeMillis();
				}
			}
		}
	}
}

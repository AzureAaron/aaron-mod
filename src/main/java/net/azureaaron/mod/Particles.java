package net.azureaaron.mod;

import java.nio.file.Files;
import java.util.HashSet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.util.Util;

public class Particles {
		
	/**This {@link HashSet} is used to avoid looking up enum constants that don't exist.*/
	public static HashSet<String> particlesSupported = Util.make(new HashSet<String>(), supportedParticles -> {
		for(ParticleConfig value : ParticleConfig.values()) {
			supportedParticles.add(value.toString());
		}
	});

	public enum ParticleConfig {
		minecraft_ash("Ash", State.FULL),
		minecraft_block_marker("Block Marker", State.FULL),
		minecraft_cherry_leaves("Cherry Leaves", State.FULL),
		minecraft_crit("Crit", State.FULL),
		minecraft_dust("Dust", State.FULL),
		minecraft_enchanted_hit("Enchanted Hit", State.FULL),
		minecraft_entity_effect("Entity Effect", State.FULL),
		minecraft_explosion("Explosion", State.FULL),
		minecraft_falling_spore_blossom("Falling Spore Blossom", State.FULL),
		minecraft_firework("Firework", State.FULL),
		minecraft_flash("Flash", State.FULL),
		minecraft_rain("Rain Splash", State.FULL),
		minecraft_spore_blossom_air("Air Spore Blossom", State.FULL),
		minecraft_white_ash("White Ash", State.FULL);
		
		public final String name;
		public State state;
		
		private ParticleConfig(String name, State state) {
			this.name = name;
			this.state = state;
		}
	}
	
	public enum State {
		FULL,
		NONE
	}
	
	protected static void init() {	
		//Load in states for the particles
		try {
			JsonObject configuredParticles = JsonParser.parseString(Files.readString(Main.CONFIG_PATH)).getAsJsonObject().get("particles").getAsJsonObject();
			if(configuredParticles != null) {
				for(String particle : configuredParticles.keySet()) {
					if(particlesSupported.contains(particle)) ParticleConfig.valueOf(particle).state = State.valueOf(configuredParticles.get(particle).getAsString());
				}
			}
		} catch (Throwable t) {
			Main.LOGGER.error("[Aaron's Mod] Failed to load particle config!");
			t.printStackTrace();
		}
	}
}

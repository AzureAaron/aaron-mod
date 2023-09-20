package net.azureaaron.mod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonObject;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import net.azureaaron.mod.util.Functions;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class Particles {
	private static final Function<Option<State>, ControllerBuilder<State>> PARTICLE_CONTROLLER = (opt) -> CyclingListControllerBuilder.create(opt)
			.values(List.of(State.values()))
			.valueFormatter(particleState -> Text.literal(Functions.titleCase(particleState.name())));
		
	/**This {@link HashSet} is used to avoid looking up enum constants that don't exist.*/
	public static HashSet<String> particlesSupported = Util.make(new HashSet<>(), supportedParticles -> {
		for (ParticleConfig value : ParticleConfig.values()) {
			supportedParticles.add(value.toString());
		}
	});

	//TODO replace this with a map records sometime
	public enum ParticleConfig {
		minecraft_ash("Ash", State.FULL, 1),
		minecraft_block_breaking("Block Breaking", State.FULL, 1), // Not a real id -- maybe split these?
		minecraft_block_marker("Block Marker", State.FULL, 1),
		minecraft_cherry_leaves("Cherry Leaves", State.FULL, 1),
		minecraft_crit("Crit", State.FULL, 1),
		minecraft_dust("Dust", State.FULL, 1),
		minecraft_enchanted_hit("Enchanted Hit", State.FULL, 1),
		minecraft_entity_effect("Entity Effect", State.FULL, 1),
		minecraft_explosion("Explosion", State.FULL, 1),
		minecraft_falling_spore_blossom("Falling Spore Blossom", State.FULL, 1),
		minecraft_firework("Firework", State.FULL, 1),
		minecraft_flash("Flash", State.FULL, 1),
		minecraft_heart("Heart", State.FULL, 1),
		minecraft_poof("Poof", State.FULL, 1),
		minecraft_rain("Rain Splash", State.FULL, 1),
		minecraft_spit("Spit", State.FULL, 1),
		minecraft_spore_blossom_air("Air Spore Blossom", State.FULL, 1),
		minecraft_white_ash("White Ash", State.FULL, 1);
		
		public final String name;
		public State state;
		public float scaleMultiplier;
		
		private ParticleConfig(String name, State state, float scaleMultiplier) {
			this.name = name;
			this.state = state;
			this.scaleMultiplier = scaleMultiplier;
		}
		
		public String getDescription() {
			return switch (this) {
				case minecraft_ash -> "Ash particles naturally generate in soul sand valleys.";
				case minecraft_block_marker -> "Block Marker particles are the particles you see for the light and barrier blocks for example.";
				case minecraft_cherry_leaves -> "The leaves that fall from cherry trees.";
				case minecraft_crit -> "These particles can be seen when a critical hit is dealt against an enemy.";
				case minecraft_dust -> "Dust particles can come in any colour! One example of their usage is the dust emitted by redstone torches.";
				case minecraft_entity_effect -> "The particles seen when an entity has an active potion effect.";
				case minecraft_enchanted_hit -> "Enchanted Hit particles can be seen when dealing damage with a weapon thats enchanted.";
				case minecraft_flash -> "Flash particles are the flash of colour you see in the air when a firework explodes.";
				case minecraft_poof -> "The particles that appear after entity deaths.";
				case minecraft_rain -> "The small splashes of water you see on the ground when it rains.";
				case minecraft_spit -> "Don't let the llamas disrespect you.";
				case minecraft_spore_blossom_air -> "The particles that float around in the air near spore blossoms.";
				case minecraft_falling_spore_blossom -> "The particles that fall down beneath spore blossoms.";
				case minecraft_white_ash -> "White Ash can be frequently found in the Basalt Deltas!";
				
				default -> null;
			};
		}
	}
	
	public enum State {
		FULL,
		NONE
	}
	
	static void init(JsonObject config) {	
		//Load in states for the particles
		try {
			JsonObject configuredParticleStates = config.get("particles").getAsJsonObject();
			if (configuredParticleStates != null) {
				for (String particle : configuredParticleStates.keySet()) {
					if (particlesSupported.contains(particle)) ParticleConfig.valueOf(particle).state = State.valueOf(configuredParticleStates.get(particle).getAsString());
				}
			}
			
			JsonObject configuredParticleScaling = config.get("particleScaling").getAsJsonObject();
			if (configuredParticleScaling != null) {
				for (String particle : configuredParticleScaling.keySet()) {
					if (particlesSupported.contains(particle)) ParticleConfig.valueOf(particle).scaleMultiplier  =configuredParticleScaling.get(particle).getAsFloat();
				}
			}
		} catch (Throwable t) {
			Main.LOGGER.error("[Aaron's Mod] Failed to load particle config!");
			t.printStackTrace();
		}
	}
	
	static List<OptionGroup> getOptionGroups() {
		List<OptionGroup> list = new ArrayList<>();
		
		for (ParticleConfig particle : ParticleConfig.values()) {
			list.add(OptionGroup.createBuilder()
					.name(Text.literal(particle.name + " Particles"))
					.description(particle.getDescription() != null ? OptionDescription.of(Text.literal(particle.getDescription())) : OptionDescription.EMPTY)
					.collapsed(true)
					
					//Toggle
					.option(Option.<State>createBuilder()
							.name(Text.literal(particle.name + " State"))
							.binding(State.FULL,
									() -> particle.state,
									newValue -> particle.state = newValue)
							.available(!Main.OPTIFABRIC_LOADED)
							.controller(PARTICLE_CONTROLLER)
							.build())
					
					//Scale Multiplier
					.option(Option.<Float>createBuilder()
							.name(Text.literal(particle.name + " Scale Multiplier"))
							.binding(1f,
									() -> particle.scaleMultiplier,
									newValue -> particle.scaleMultiplier = newValue)
							.available(!Main.OPTIFABRIC_LOADED)
							.controller(opt -> FloatFieldControllerBuilder.create(opt).range(0f, 2f))
							.build())
					.build());
		}
		
		return list;
	}
}

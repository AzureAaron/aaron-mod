package net.azureaaron.mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.azureaaron.dandelion.api.Option;
import net.azureaaron.dandelion.api.OptionGroup;
import net.azureaaron.dandelion.api.controllers.FloatController;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.config.ConfigUtils;
import net.azureaaron.mod.mixins.accessors.SingleQuadParticleAccessor;
import net.azureaaron.mod.utils.Functions;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;

public class Particles {
	private static final Reference2ObjectOpenHashMap<ParticleType<?>, String> PARTICLE_DESCRIPTIONS = Util.make(new Reference2ObjectOpenHashMap<>(), descriptions -> {
		descriptions.put(ParticleTypes.ASH, "Ash particles naturally generate in soul sand valleys.");
		descriptions.put(ParticleTypes.BLOCK_MARKER, "Block Marker particles are the particles you see for the light and barrier blocks for example.");
		descriptions.put(ParticleTypes.CHERRY_LEAVES, "The leaves that fall from cherry trees.");
		descriptions.put(ParticleTypes.CRIT, "These particles can be seen when a critical hit is dealt against an enemy.");
		descriptions.put(ParticleTypes.DUST, "Dust particles can come in any colour! One example of their usage is the dust emitted by redstone torches.");
		descriptions.put(ParticleTypes.ENTITY_EFFECT, "The particles seen when an entity has an active potion effect.");
		descriptions.put(ParticleTypes.ENCHANTED_HIT, "Enchanted Hit particles can be seen when dealing damage with a weapon thats enchanted.");
		descriptions.put(ParticleTypes.FLASH, "Flash particles are the flash of colour you see in the air when a firework explodes.");
		descriptions.put(ParticleTypes.POOF, "The particles that appear after entity deaths.");
		descriptions.put(ParticleTypes.RAIN, "The small splashes of water you see on the ground when it rains.");
		descriptions.put(ParticleTypes.SPIT, "Don't let the llamas disrespect you.");
		descriptions.put(ParticleTypes.SPORE_BLOSSOM_AIR, "The particles that float around in the air near spore blossoms.");
		descriptions.put(ParticleTypes.FALLING_SPORE_BLOSSOM, "The particles that fall down beneath spore blossoms.");
		descriptions.put(ParticleTypes.WHITE_ASH, "White Ash can be frequently found in the Basalt Deltas!");
	});

	/**
	 * Applies modifications to particles including scaling and alpha.
	 */
	public static Particle modifyParticle(Particle particle, Identifier id) {
		float alpha = AaronModConfigManager.get().particles.alphas.getOrDefault(id, 1f);
		float scale = AaronModConfigManager.get().particles.scaling.getOrDefault(id, 1f);

		//Only set the alpha if won't result in the particle being discarded by the fragment shader or if its not greater than the default
		if (particle instanceof SingleQuadParticle billboard && billboard instanceof SingleQuadParticleAccessor accessor && alpha > 0.1f && alpha < accessor.getAlpha()) {
			accessor.invokeSetAlpha(alpha);
			billboard.markHasCustomAlpha();
		}

		return (scale != 1f) ? particle.scale(scale) : particle;
	}

	private static String getParticleDisplayName(String id) {
		return Functions.titleCase(id.toString().replace("_", " "));
	}

	public static List<OptionGroup> getOptionGroups(AaronModConfig config) {
		List<OptionGroup> list = new ArrayList<>();
		List<Entry<ResourceKey<ParticleType<?>>, ParticleType<?>>> entryList = new ArrayList<>(BuiltInRegistries.PARTICLE_TYPE.entrySet());

		// Alphabetically sort the entries for logical ordering
		entryList.sort((o1, o2) -> {
			String o1Name = getParticleDisplayName(o1.getKey().identifier().toString());
			String o2Name = getParticleDisplayName(o2.getKey().identifier().toString());

			return o1Name.compareTo(o2Name);
		});

		for (Entry<ResourceKey<ParticleType<?>>, ParticleType<?>> entry : entryList) {
			ParticleType<?> particleType = entry.getValue();
			Identifier id = entry.getKey().identifier();

			String name = getParticleDisplayName(id.getPath());
			String namespaceName = getParticleDisplayName(id.getNamespace());
			Component description = PARTICLE_DESCRIPTIONS.containsKey(particleType) ? Component.literal(PARTICLE_DESCRIPTIONS.get(particleType)) : Component.empty();

			list.add(OptionGroup.createBuilder()
					.name(Component.literal(name + " Particles (" + namespaceName + ")"))
					.description(description)
					.collapsed(true)

					//Toggle
					.option(Option.<Boolean>createBuilder()
							.name(Component.literal("Enable " + name))
							.binding(true,
									() -> config.particles.states.getOrDefault(id, true),
									newValue -> config.particles.states.put(id, newValue.booleanValue()))
							.modifiable(!Main.OPTIFABRIC_LOADED)
							.controller(ConfigUtils.createBooleanController())
							.build())
					.option(Option.<Float>createBuilder()
							.name(Component.literal(name + " Scale Multiplier"))
							.binding(1f,
									() -> config.particles.scaling.getOrDefault(id, 1f),
									newValue -> config.particles.scaling.put(id, newValue.floatValue()))
							.modifiable(!Main.OPTIFABRIC_LOADED)
							.controller(FloatController.createBuilder().range(0f, 20f).build())
							.build())
					.option(Option.<Float>createBuilder()
							.name(Component.literal(name + " Opacity"))
							.binding(1f,
									() -> config.particles.alphas.getOrDefault(id, 1f),
									newValue -> config.particles.alphas.put(id, newValue.floatValue()))
							.controller(FloatController.createBuilder().range(0.15f, 1f).slider(0.05f).build())
							.modifiable(!Main.OPTIFABRIC_LOADED)
							.build())
					.build());
		}

		return list;
	}
}

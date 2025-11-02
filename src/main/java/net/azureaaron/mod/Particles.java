package net.azureaaron.mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.azureaaron.dandelion.systems.Option;
import net.azureaaron.dandelion.systems.OptionGroup;
import net.azureaaron.dandelion.systems.controllers.FloatController;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.config.ConfigUtils;
import net.azureaaron.mod.mixins.accessors.BillboardParticleAccessor;
import net.azureaaron.mod.utils.Functions;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
		if (particle instanceof BillboardParticle billboard && billboard instanceof BillboardParticleAccessor accessor && alpha > 0.1f && alpha < accessor.getAlpha()) {
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
		List<Entry<RegistryKey<ParticleType<?>>, ParticleType<?>>> entryList = new ArrayList<>(Registries.PARTICLE_TYPE.getEntrySet());

		// Alphabetically sort the entries for logical ordering
		entryList.sort((o1, o2) -> {
			String o1Name = getParticleDisplayName(o1.getKey().getValue().toString());
			String o2Name = getParticleDisplayName(o2.getKey().getValue().toString());

			return o1Name.compareTo(o2Name);
		});

		for (Entry<RegistryKey<ParticleType<?>>, ParticleType<?>> entry : entryList) {
			ParticleType<?> particleType = entry.getValue();
			Identifier id = entry.getKey().getValue();

			String name = getParticleDisplayName(id.getPath());
			String namespaceName = getParticleDisplayName(id.getNamespace());
			Text description = PARTICLE_DESCRIPTIONS.containsKey(particleType) ? Text.literal(PARTICLE_DESCRIPTIONS.get(particleType)) : Text.empty();

			list.add(OptionGroup.createBuilder()
					.name(Text.literal(name + " Particles (" + namespaceName + ")"))
					.description(description)
					.collapsed(true)

					//Toggle
					.option(Option.<Boolean>createBuilder()
							.name(Text.literal("Enable " + name))
							.binding(true,
									() -> config.particles.states.getOrDefault(id, true),
									newValue -> config.particles.states.put(id, newValue.booleanValue()))
							.modifiable(!Main.OPTIFABRIC_LOADED)
							.controller(ConfigUtils.createBooleanController())
							.build())
					.option(Option.<Float>createBuilder()
							.name(Text.literal(name + " Scale Multiplier"))
							.binding(1f,
									() -> config.particles.scaling.getOrDefault(id, 1f),
									newValue -> config.particles.scaling.put(id, newValue.floatValue()))
							.modifiable(!Main.OPTIFABRIC_LOADED)
							.controller(FloatController.createBuilder().range(0f, 20f).build())
							.build())
					.option(Option.<Float>createBuilder()
							.name(Text.literal(name + " Opacity"))
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

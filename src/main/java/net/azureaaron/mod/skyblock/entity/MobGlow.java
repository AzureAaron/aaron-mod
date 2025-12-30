package net.azureaaron.mod.skyblock.entity;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.azureaaron.mod.annotations.Init;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.decoration.ArmorStand;

public class MobGlow {
	/**
	 * Default value for when a mob should not have custom glow applied.
	 */
	public static final int NO_GLOW = EntityRenderState.NO_OUTLINE;
	/**
	 * Attached to {@code EntityRenderState}s to apply the custom glow colour.
	 */
	public static final RenderStateDataKey<Integer> ENTITY_CUSTOM_GLOW_COLOUR = RenderStateDataKey.create(() -> "Aaron Mod custom glow colour");
	/**
	 * Attached to {@code WorldRenderState}s to indicate that the custom glow is being used this frame.
	 */
	public static final RenderStateDataKey<Boolean> FRAME_USES_CUSTOM_GLOW = RenderStateDataKey.create(() -> "Aaron Mod frame uses custom glow");
	private static final List<MobGlowAdder> ADDERS = new ArrayList<>();
	private static final Object2IntMap<Entity> CACHE = new Object2IntOpenHashMap<>();

	@Init
	public static void init() {
		// Have the cache cleared every tick to ensure correctness
		ClientTickEvents.END_CLIENT_TICK.register(_client -> reset());
	}

	/**
	 * Registers a glow adder, automatically done by the {@link MobGlowAdder} constructor.
	 */
	protected static void registerGlowAdder(MobGlowAdder adder) {
		ADDERS.add(adder);
	}

	public static boolean hasOrComputeMobGlow(Entity entity) {
		if (CACHE.containsKey(entity)) {
			return true;
		}

		int colour = computeMobGlow(entity);
		if (colour != NO_GLOW) {
			CACHE.put(entity, colour);
			return true;
		}

		return false;
	}

	public static int getMobGlowOrDefault(Entity entity, int defaultColour) {
		return CACHE.getOrDefault(entity, defaultColour);
	}

	public static int getMobGlow(Entity entity) {
		return CACHE.getInt(entity);
	}

	/**
	 * Computes the glow colour for the respective entity.
	 */
	private static int computeMobGlow(Entity entity) {
		for (MobGlowAdder adder : ADDERS) {
			if (adder.isEnabled()) {
				int glowColour = adder.computeColour(entity);

				if (glowColour != NO_GLOW) return glowColour;
			}
		}

		return NO_GLOW;
	}

	private static void reset() {
		CACHE.clear();
	}

	/*
	 * Useful for checking entity name armour stands.
	 */
	public static List<ArmorStand> getNearbyArmourStands(Entity entity) {
		return entity.level().getEntitiesOfClass(ArmorStand.class, entity.getBoundingBox().inflate(0, 2, 0), EntitySelector.ENTITY_NOT_BEING_RIDDEN);
	}
}

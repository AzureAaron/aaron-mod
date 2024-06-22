package net.azureaaron.mod.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.entity.ClientEntityManager;

@Mixin(ClientWorld.class)
public interface ClientWorldAccessor {

	@Accessor
	ClientEntityManager<Entity> getEntityManager();
}

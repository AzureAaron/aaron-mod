package net.azureaaron.mod.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.world.ClientEntityManager;
import net.minecraft.world.entity.EntityIndex;
import net.minecraft.world.entity.EntityLike;

@Mixin(ClientEntityManager.class)
public interface ClientEntityManagerAccessor<T extends EntityLike> {

	@Accessor
	EntityIndex<T> getIndex();
}

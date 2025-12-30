package net.azureaaron.mod.mixins.accessors;

import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TransientEntitySectionManager.class)
public interface ClientEntityManagerAccessor<T extends EntityAccess> {

	@Accessor
	EntityLookup<T> getEntityStorage();
}

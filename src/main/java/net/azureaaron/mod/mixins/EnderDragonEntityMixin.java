package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.Cache;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.world.World;

@Mixin(EnderDragonEntity.class)
public class EnderDragonEntityMixin extends MobEntity implements Monster {

	protected EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}
	
	@Override
	public boolean isGlowing() {
		if(Cache.inM7Phase5 && Config.glowingM7Dragons) return true;
		return super.isGlowing();
	}
}

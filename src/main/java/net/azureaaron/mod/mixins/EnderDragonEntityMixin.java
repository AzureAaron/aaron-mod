package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.Config;
import net.azureaaron.mod.features.BoundingBoxes;
import net.azureaaron.mod.util.Cache;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

@Mixin(EnderDragonEntity.class)
public class EnderDragonEntityMixin extends MobEntity implements Monster {
	private static final int POWER_COLOUR = 0xe02b2b;
	private static final int FLAME_COLOUR = 0xe87c46;
	private static final int APEX_COLOUR = 0x168a16;
	private static final int ICE_COLOUR = 0x18d2db;
	private static final int SOUL_COLOUR = 0x8d18db;
	
	protected EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}
	
	@Inject(method = "onSpawnPacket", at = @At("TAIL"))
	private void aaronMod$determineDragonColour() {
		if(Cache.inM7Phase5) {
			Box dragonBoundingBox = this.calculateBoundingBox();
			Box powerBoundingBox = new Box(BoundingBoxes.Dragons.POWER.pos1, BoundingBoxes.Dragons.POWER.pos2);
			Box flameBoundingBox = new Box(BoundingBoxes.Dragons.FLAME.pos1, BoundingBoxes.Dragons.FLAME.pos2);
			Box apexBoundingBox = new Box(BoundingBoxes.Dragons.APEX.pos1, BoundingBoxes.Dragons.APEX.pos2);
			Box iceBoundingBox = new Box(BoundingBoxes.Dragons.ICE.pos1, BoundingBoxes.Dragons.ICE.pos2);
			Box soulBoundingBox = new Box(BoundingBoxes.Dragons.SOUL.pos1, BoundingBoxes.Dragons.SOUL.pos2);
			
			if(dragonBoundingBox.intersects(powerBoundingBox)) Cache.powerDragonUuid = this.getUuidAsString();
			if(dragonBoundingBox.intersects(flameBoundingBox)) Cache.flameDragonUuid = this.getUuidAsString();
			if(dragonBoundingBox.intersects(apexBoundingBox)) Cache.apexDragonUuid = this.getUuidAsString();
			if(dragonBoundingBox.intersects(iceBoundingBox)) Cache.iceDragonUuid = this.getUuidAsString();
			if(dragonBoundingBox.intersects(soulBoundingBox)) Cache.soulDragonUuid = this.getUuidAsString();
		}
	}
	
	@Override
	public int getTeamColorValue() {
		if(Cache.inM7Phase5) {
			if(this.getUuidAsString().equals(Cache.powerDragonUuid)) return POWER_COLOUR;
			if(this.getUuidAsString().equals(Cache.flameDragonUuid)) return FLAME_COLOUR;
			if(this.getUuidAsString().equals(Cache.apexDragonUuid)) return APEX_COLOUR;
			if(this.getUuidAsString().equals(Cache.iceDragonUuid)) return ICE_COLOUR;
			if(this.getUuidAsString().equals(Cache.soulDragonUuid)) return SOUL_COLOUR;
		}
		return super.getTeamColorValue();
	}
	
	@Override
	public boolean isGlowing() {
		if(Cache.inM7Phase5 && Config.glowingM7Dragons) return true;
		return super.isGlowing();
	}
}

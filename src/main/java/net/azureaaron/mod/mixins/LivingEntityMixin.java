package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	protected LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@ModifyExpressionValue(method = "getCurrentSwingDuration",
			at = { @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectUtil;hasDigSpeed(Lnet/minecraft/world/entity/LivingEntity;)Z"),
					@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z") },
			require = 2
	)
	private boolean aaronMod$ignoreMiningEffects(boolean original) {
		return shouldEnableSwingModifications() && AaronModConfigManager.get().itemModel.ignoreMiningEffects ? false : original;
	}

	@ModifyVariable(method = "getCurrentSwingDuration", at = @At("LOAD"), ordinal = 0)
	private int aaronMod$modifySwingDuration(int original) {
		return shouldEnableSwingModifications() ? AaronModConfigManager.get().itemModel.swingDuration : original;
	}

	@Unique
	private boolean shouldEnableSwingModifications() {
		return AaronModConfigManager.get().itemModel.enableItemModelCustomization && (Entity) this == Minecraft.getInstance().player;
	}
}

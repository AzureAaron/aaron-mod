package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {

	protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef,
			DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry,
			boolean isClient, boolean debugWorld, long biomeAccess,
			int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, biomeAccess,
				maxChainedNeighborUpdates);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;tickTime()V", shift = At.Shift.BEFORE))
	private void aaronMod$correctAmbientDarkness(CallbackInfo ci) {
		if (AaronModConfigManager.get().correctAmbientDarkness) this.calculateAmbientDarkness();
	}

	@ModifyExpressionValue(method = "getLightningTicksLeft", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z", remap = false))
	private boolean aaronMod$hideLightningFlashes(boolean shouldHideLightningFlashes) {
		return shouldHideLightningFlashes || AaronModConfigManager.get().hideLightning;
	}
}

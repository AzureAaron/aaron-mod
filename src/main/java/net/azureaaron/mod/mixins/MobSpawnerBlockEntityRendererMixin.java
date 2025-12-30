package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.client.renderer.blockentity.state.SpawnerRenderState;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

@Mixin(SpawnerRenderer.class)
public abstract class MobSpawnerBlockEntityRendererMixin implements BlockEntityRenderer<SpawnerBlockEntity, SpawnerRenderState> {

	@Inject(method = "submit", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideSpinningMobInSpawner(CallbackInfo ci) {
		if (AaronModConfigManager.get().uiAndVisuals.world.hideMobSpawnerAnimations) ci.cancel();
	}
}

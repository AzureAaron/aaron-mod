package net.azureaaron.mod.mixins;

import java.util.Calendar;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class ChestBlockEntityRendererMixin<T extends BlockEntity> implements BlockEntityRenderer<T> {
	
	@Shadow private boolean christmas;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRendererFactory$Context;getLayerModelPart(Lnet/minecraft/client/render/entity/model/EntityModelLayer;)Lnet/minecraft/client/model/ModelPart;", ordinal = 0, shift = At.Shift.BEFORE))
	private void aaronMod$decemberChristmasChests() {
		Calendar calendar = Calendar.getInstance();
		
		if(!this.christmas && AaronModConfigManager.get().decemberChristmasChests && calendar.get(Calendar.MONTH) + 1 == 12) this.christmas = true;
	}
}

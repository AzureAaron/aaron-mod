package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.azureaaron.mod.Keybinds;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.world.level.block.entity.SignBlockEntity;

@Mixin(AbstractSignRenderer.class)
public abstract class AbstractSignRendererMixin implements BlockEntityRenderer<SignBlockEntity, SignRenderState> {

	@ModifyReturnValue(method = "isOutlineVisible", at = @At("RETURN"))
	private static boolean aaronMod$renderGlowingTextOutlineWhenZoomedIn(boolean renderGlowingOutline) {
		return Keybinds.zoomKeybind.isDown() ? true : renderGlowingOutline;
	}
}

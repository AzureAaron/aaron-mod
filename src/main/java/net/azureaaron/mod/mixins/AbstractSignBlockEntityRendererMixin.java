package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.azureaaron.mod.Keybinds;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.SignBlockEntityRenderState;

@Mixin(AbstractSignBlockEntityRenderer.class)
public abstract class AbstractSignBlockEntityRendererMixin implements BlockEntityRenderer<SignBlockEntity, SignBlockEntityRenderState> {

	@ModifyReturnValue(method = "shouldRenderTextOutline", at = @At("RETURN"))
	private static boolean aaronMod$renderGlowingTextOutlineWhenZoomedIn(boolean renderGlowingOutline) {
		return Keybinds.zoomKeybind.isPressed() ? true : renderGlowingOutline;
	}
}

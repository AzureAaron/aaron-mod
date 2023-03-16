package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.azureaaron.mod.Keybinds;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;

@Mixin(SignBlockEntityRenderer.class)
public abstract class SignBlockEntityRendererMixin implements BlockEntityRenderer<SignBlockEntity> {
	
	//The target method determines whether the outlines of glowing text on glowing signs should render or not,
	//it has nothing to do with whether the actual sign itself should render.
	@ModifyReturnValue(method = "shouldRender", at = @At("RETURN"))
	private static boolean aaronMod$renderGlowWhenZoomedIn(boolean renderGlowingOutline) {
		return Keybinds.zoomKeybind.isPressed() ? true : renderGlowingOutline;
	}
}

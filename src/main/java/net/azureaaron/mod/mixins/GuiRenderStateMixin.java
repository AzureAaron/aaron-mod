package net.azureaaron.mod.mixins;

import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.state.GuiRenderState;

@Mixin(GuiRenderState.class)
public class GuiRenderStateMixin {
	@Unique
	private int savedGuiDepth;

	@Inject(method = "forEachSimpleElement", at = @At("TAIL"))
	private void aaronMod$saveGuiDepth(CallbackInfo ci, @Local MutableInt depth) {
		this.savedGuiDepth = depth.getValue() + 800; //Random offset to ensure it works
	}

	@ModifyArg(method = "forEachSimpleElement", at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/mutable/MutableInt;<init>(I)V"))
	private int aaronMod$useSaveDepth(int defaultDepth) {
		return MinecraftClient.getInstance().gameRenderer.shouldUseSavedGuiDepth() ? this.savedGuiDepth : defaultDepth;
	}
}

package net.azureaaron.mod.mixins;

import java.awt.Color;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.azureaaron.mod.features.ChromaText;

@Mixin(targets = "de.hysky.skyblocker.skyblock.profileviewer.collections.GenericCategory", remap = false)
@Pseudo
public class SkyblockerPVCollectionsGenericCategoryMixin {

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V", ordinal = 1, remap = true), index = 4, require = 0)
	private int aaronMod$maxCollectionsChromaText(int colour) {
		return colour == Color.MAGENTA.getRGB() && ChromaText.chromaColourAvailable() ? 0xFFAA5500 : colour;
	}
}

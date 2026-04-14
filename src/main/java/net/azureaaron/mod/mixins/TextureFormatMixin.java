package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.textures.TextureFormat;

@Mixin(TextureFormat.class)
public enum TextureFormatMixin {
	AARON_MOD$RGBA32F(Float.BYTES * 4);

	@Shadow
	TextureFormatMixin(final int pixelSize) {}
}

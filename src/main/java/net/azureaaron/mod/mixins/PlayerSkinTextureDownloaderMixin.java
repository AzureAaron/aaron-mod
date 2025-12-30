package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.renderer.texture.SkinTextureDownloader;

@Mixin(SkinTextureDownloader.class)
public class PlayerSkinTextureDownloaderMixin {

	@ModifyVariable(method = "downloadSkin", at = @At("LOAD"), argsOnly = true, ordinal = 0)
	private static String aaronMod$secureSkinDownloads(String uri) {
		if (AaronModConfigManager.get().refinements.secureSkinDownloads) {
			return uri.replace("http://", "https://");
		} else {
			return uri;
		}
	}
}

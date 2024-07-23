package net.azureaaron.mod.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.texture.PlayerSkinTexture;

@Mixin(PlayerSkinTexture.class)
public class PlayerSkinTextureMixin {

	@ModifyExpressionValue(method = "method_22801", at = @At(value = "FIELD", target = "Lnet/minecraft/client/texture/PlayerSkinTexture;url:Ljava/lang/String;", opcode = Opcodes.GETFIELD))
	private String aaronMod$secureSkinDownloads(String url) {		
		if (AaronModConfigManager.get().secureSkinDownloads) {
			return url.replace("http://", "https://");
		} else {
			return url;
		}
	}
}

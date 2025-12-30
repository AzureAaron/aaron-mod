package net.azureaaron.mod.mixins;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.server.packs.PathPackResources;

@Mixin(PathPackResources.class)
public class PathPackResourcesMixin {

	@WrapWithCondition(method = "listPath(Ljava/lang/String;Ljava/nio/file/Path;Ljava/util/List;Lnet/minecraft/server/packs/PackResources$ResourceOutput;)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false))
	private static boolean aaronMod$silenceInvalidDirectoryErrors(Logger logger, String message, Object path, Object ioException) {
		return !AaronModConfigManager.get().refinements.silenceResourcePackLogSpam;
	}

	@WrapWithCondition(method = "getNamespaces", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false))
	private boolean aaronMod$silenceDsStoreWarnings(Logger logger, String message, Object fileName, Object root) {
		return !AaronModConfigManager.get().refinements.silenceResourcePackLogSpam && ((String) fileName).equals(".DS_Store");
	}
}

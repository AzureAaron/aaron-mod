package net.azureaaron.mod.mixins;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import net.azureaaron.mod.Config;
import net.minecraft.resource.DirectoryResourcePack;

@Mixin(DirectoryResourcePack.class)
public class DirectoryResourcePackMixin {

	@WrapWithCondition(method = "findResources(Ljava/lang/String;Ljava/nio/file/Path;Ljava/util/List;Lnet/minecraft/resource/ResourcePack$ResultConsumer;)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
	private static boolean aaronMod$silenceInvalidDirectoryErrors(Logger logger, String message, Object path, Object ioException) {
		return !Config.silenceResourcePackLogSpam;
	}
}

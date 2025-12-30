package net.azureaaron.mod.mixins;

import java.util.Map;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.features.ChromaText;
import net.azureaaron.mod.utils.Cache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.Resource;

@Mixin(ShaderManager.class)
public class ShaderManagerMixin {
	@Unique
	private static final String RENDERTYPE_TEXT_SHADER = "shaders/core/rendertype_text";
	@Shadow
	@Final
	static Logger LOGGER;

	@ModifyExpressionValue(method = "prepare",
			at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;", ordinal = 0),
			slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/shaders/ShaderType;byLocation(Lnet/minecraft/resources/Identifier;)Lcom/mojang/blaze3d/shaders/ShaderType;"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ShaderManager;loadShader(Lnet/minecraft/resources/Identifier;Lnet/minecraft/server/packs/resources/Resource;Lcom/mojang/blaze3d/shaders/ShaderType;Ljava/util/Map;Lcom/google/common/collect/ImmutableMap$Builder;)V"))
	)
	private Object aaronMod$useVanillaRenderTypeTextShaderWhileOnMCCI(Object genericResource, @Local Map.Entry<Identifier, Resource> entry) {
		if (Cache.currentServerAddress.endsWith("mccisland.net") && entry.getValue().sourcePackId().equals(ChromaText.ID.toString()) && entry.getKey().getPath().startsWith(RENDERTYPE_TEXT_SHADER)) {
			LOGGER.warn("[Aaron's Mod] Loading vanilla {} shader file due to an incompatibility on MCCI", entry.getKey());

			VanillaPackResources vanillaPack = Minecraft.getInstance().getVanillaPackResources();

			return new Resource(vanillaPack, vanillaPack.getResource(PackType.CLIENT_RESOURCES, entry.getKey()));
		}

		return genericResource;
	}
}

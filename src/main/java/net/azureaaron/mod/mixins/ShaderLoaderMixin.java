package net.azureaaron.mod.mixins;

import java.util.Map;
import java.util.Set;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgramDefinition;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

@Mixin(ShaderLoader.class)
public class ShaderLoaderMixin {
	@Unique
	private static final String RENDERTYPE_TEXT_SHADER = "shaders/core/rendertype_text";
	@Unique
	private static final Defines CHROMA_DEFINES = new Defines(Map.of(), Set.of("AARON_MOD_CHROMA"));
	@Shadow
	@Final
	static Logger LOGGER;

	@ModifyExpressionValue(method = "prepare",
			at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;", remap = false),
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/CompiledShader$Type;fromId(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/gl/CompiledShader$Type;"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/ShaderLoader;loadDefinition(Lnet/minecraft/util/Identifier;Lnet/minecraft/resource/Resource;Lcom/google/common/collect/ImmutableMap$Builder;)V"))
	)
	private Object aaronMod$useVanillaRenderTypeTextShaderWhileOnMCCI(Object genericResource, @Local Map.Entry<Identifier, Resource> entry) {
		if (Cache.currentServerAddress.endsWith("mccisland.net") && entry.getValue().getPackId().equals(ChromaText.ID.toString()) && entry.getKey().getPath().startsWith(RENDERTYPE_TEXT_SHADER)) {
			LOGGER.warn("[Aaron's Mod] Loading vanilla {} shader file due to an incompatibility on MCCI", entry.getKey());

			DefaultResourcePack vanillaPack = MinecraftClient.getInstance().getDefaultResourcePack();

			return new Resource(vanillaPack, vanillaPack.open(ResourceType.CLIENT_RESOURCES, entry.getKey()));
		}

		return genericResource;
	}

	//This would've been a @ModifyVariable targeting the STORE but I ran into a VerifyError?
	@ModifyExpressionValue(method = "loadDefinition", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/DataResult;getOrThrow(Ljava/util/function/Function;)Ljava/lang/Object;", remap = false))
	private static Object aaronMod$injectChromaDefineFlag(Object genericShaderProgramDefinition, @Local(argsOnly = true) Identifier id) {
		if (id.getPath().startsWith(RENDERTYPE_TEXT_SHADER)) {
			ShaderProgramDefinition programDefinition = (ShaderProgramDefinition) genericShaderProgramDefinition;
			Defines newDefines = programDefinition.defines().withMerged(CHROMA_DEFINES);

			return new ShaderProgramDefinition(programDefinition.vertex(), programDefinition.fragment(), programDefinition.samplers(), programDefinition.uniforms(), newDefines);
			
		}

		return genericShaderProgramDefinition;
	}
}

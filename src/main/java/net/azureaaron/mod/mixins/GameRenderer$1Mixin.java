package net.azureaaron.mod.mixins;

import java.io.InputStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.features.ChromaText;
import net.azureaaron.mod.utils.Cache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

@Mixin(targets = "net.minecraft.client.render.GameRenderer$1")
public class GameRenderer$1Mixin {
	private static final Identifier RENDER_TYPE_TEXT_FSH_ID = new Identifier("minecraft", "shaders/core/rendertype_text.fsh");

	@Redirect(method = "method_45778", at = @At(value = "NEW", target = "Lnet/minecraft/resource/Resource;"))
	private static Resource aaronMod$dontLoadShaderWhileOnMCCI(ResourcePack pack, InputSupplier<InputStream> supplier, @Local Identifier id) {
		if (Cache.currentServerAddress.endsWith("mccisland.net") && pack.getId().equals(ChromaText.ID.toString()) && id.equals(RENDER_TYPE_TEXT_FSH_ID)) {
			Main.LOGGER.warn("[Aaron's Mod] Loading vanilla rendertype_text.fsh shader due to an incompatibility on MCCI");
			
			DefaultResourcePack vanillaPack = MinecraftClient.getInstance().getDefaultResourcePack();
			return new Resource(vanillaPack, vanillaPack.open(ResourceType.CLIENT_RESOURCES, RENDER_TYPE_TEXT_FSH_ID));
		}
		
		return new Resource(pack, supplier);
	}
}

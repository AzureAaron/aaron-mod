package net.azureaaron.mod.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.fog.FogRenderer;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

	@Accessor
	FogRenderer getFogRenderer();
}

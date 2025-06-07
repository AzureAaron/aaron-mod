package net.azureaaron.mod.mixins.accessors;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;

@Mixin(FogRenderer.class)
public interface FogRendererAccessor {

	@Accessor
	static List<FogModifier> getFOG_MODIFIERS() {
		throw new UnsupportedOperationException();
	}

	@Invoker
	CameraSubmersionType invokeGetCameraSubmersionType(Camera camera, boolean thick);
}

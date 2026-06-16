package net.azureaaron.mod.mixins;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.injected.GlowRenderTypeHolder;
import net.azureaaron.mod.mixins.accessors.RenderSetupAccessor;
import net.azureaaron.mod.utils.render.AaronModRenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

@Mixin(RenderType.class)
public class RenderTypeMixin implements GlowRenderTypeHolder {
	@Unique
	private static final BiFunction<Identifier, Boolean, RenderType> OUTLINE_DEPTH = Util.memoize(
			((texture, cullState) -> RenderType.create(
					"skyblocker outline depth",
					RenderSetup.builder(cullState ? AaronModRenderPipelines.OUTLINE_DEPTH_CULL : AaronModRenderPipelines.OUTLINE_DEPTH_NO_CULL)
					.withTexture("Sampler0", texture)
					.setOutputTarget(OutputTarget.OUTLINE_TARGET)
					.setOutline(RenderSetup.OutlineProperty.IS_OUTLINE)
					.createRenderSetup()
					))
			);
	@Unique
	private @Nullable Optional<RenderType> outlineDepth;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void skyblocker$initOutlineDepthRenderType(CallbackInfo ci, @Local(name = "state") RenderSetup state) {
		RenderSetupAccessor accessor = (RenderSetupAccessor) (Object) state;
		this.outlineDepth = accessor.getOutlineProperty() == RenderSetup.OutlineProperty.AFFECTS_OUTLINE ? accessor.getTextures()
				.values()
				.stream()
				.findFirst()
				.map(texture -> OUTLINE_DEPTH.apply(texture.location(), accessor.getPipeline().isCull()))
				: Optional.empty();
	}

	@Override
	public Optional<RenderType> aaronMod$getGlowRenderType() {
		return Objects.requireNonNull(this.outlineDepth);
	}
}

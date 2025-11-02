package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.azureaaron.mod.injected.CustomGlowState;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;

@Mixin(value = { OrderedRenderCommandQueueImpl.ItemCommand.class, OrderedRenderCommandQueueImpl.ModelCommand.class, OrderedRenderCommandQueueImpl.ModelPartCommand.class })
public class RenderCommandMixin implements CustomGlowState {
	@Unique
	private boolean hasCustomGlow = false;

	@Override
	public void aaronMod$markCustomGlow() {
		this.hasCustomGlow = true;
	}

	@Override
	public boolean aaronMod$hasCustomGlow() {
		return this.hasCustomGlow;
	}
}

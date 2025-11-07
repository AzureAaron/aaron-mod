package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.azureaaron.mod.injected.CustomGlowState;
import net.azureaaron.mod.skyblock.entity.MobGlow;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;

@Mixin(value = { OrderedRenderCommandQueueImpl.ItemCommand.class, OrderedRenderCommandQueueImpl.ModelCommand.class, OrderedRenderCommandQueueImpl.ModelPartCommand.class })
public class RenderCommandMixin implements CustomGlowState {
	@Unique
	private int customGlowColour = MobGlow.NO_GLOW;

	@Override
	public void aaronMod$setCustomGlowColour(int glowColour) {
		this.customGlowColour = glowColour;
	}

	@Override
	public int aaronMod$getCustomGlowColour() {
		return this.customGlowColour;
	}
}

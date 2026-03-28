package net.azureaaron.mod.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.state.gui.GuiRenderState;

@Mixin(GuiRenderer.class)
public interface GuiRendererAccessor {
	@Accessor
	@Mutable
	void setRenderState(GuiRenderState renderState);
}

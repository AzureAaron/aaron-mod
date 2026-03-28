package net.azureaaron.mod.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;

@Mixin(GameRenderState.class)
public interface GameRenderStateAccessor {
	@Accessor
	@Mutable
	void setGuiRenderState(GuiRenderState guiRenderState);
}

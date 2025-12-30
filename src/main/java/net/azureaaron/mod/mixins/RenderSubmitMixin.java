package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.azureaaron.mod.injected.CustomGlowState;
import net.azureaaron.mod.skyblock.entity.MobGlow;
import net.minecraft.client.renderer.SubmitNodeStorage;

@Mixin(value = { SubmitNodeStorage.ItemSubmit.class, SubmitNodeStorage.ModelSubmit.class, SubmitNodeStorage.ModelPartSubmit.class })
public class RenderSubmitMixin implements CustomGlowState {
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

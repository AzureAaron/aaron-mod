package net.azureaaron.mod.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.ProfileKeys;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {

	@Accessor
	ProfileKeys getProfileKeys();
}

package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.GuiMessageTag;

@Mixin(GuiMessageTag.class)
public class MessageIndicatorMixin {
	@Unique
	private static final int OLD_NOT_SECURE_COLOUR = 15224664;
	@Unique
	private static final int OLD_MODIFIED_COLOUR = 15386724;

	@ModifyReturnValue(method = "chatNotSecure", at = @At("RETURN"))
	private static GuiMessageTag aaronMod$changeNotSecureColour(GuiMessageTag original) {
		return AaronModConfigManager.get().uiAndVisuals.legacyRevival.oldMessageTrustIndicatorColours ? new GuiMessageTag(OLD_NOT_SECURE_COLOUR, original.icon(), original.text(), original.logTag()) : original;
	}

	@ModifyArg(method = "chatModified", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GuiMessageTag;<init>(ILnet/minecraft/client/GuiMessageTag$Icon;Lnet/minecraft/network/chat/Component;Ljava/lang/String;)V"))
	private static int aaronMod$changeModifiedColour(int colour) {
		return AaronModConfigManager.get().uiAndVisuals.legacyRevival.oldMessageTrustIndicatorColours ? OLD_MODIFIED_COLOUR : colour;
	}
}

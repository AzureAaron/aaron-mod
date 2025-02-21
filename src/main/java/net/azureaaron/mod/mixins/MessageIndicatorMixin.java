package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.gui.hud.MessageIndicator;

@Mixin(MessageIndicator.class)
public class MessageIndicatorMixin {
	@Unique
	private static final int OLD_NOT_SECURE_COLOUR = 15224664;
	@Unique
	private static final int OLD_MODIFIED_COLOUR = 15386724;

	@ModifyReturnValue(method = "notSecure", at = @At("RETURN"))
	private static MessageIndicator aaronMod$changeNotSecureColour(MessageIndicator original) {
		return AaronModConfigManager.get().uiAndVisuals.legacyRevival.oldMessageTrustIndicatorColours ? new MessageIndicator(OLD_NOT_SECURE_COLOUR, original.icon(), original.text(), original.loggedName()) : original;
	}

	@ModifyArg(method = "modified", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/MessageIndicator;<init>(ILnet/minecraft/client/gui/hud/MessageIndicator$Icon;Lnet/minecraft/text/Text;Ljava/lang/String;)V"))
	private static int aaronMod$changeModifiedColour(int colour) {
		return AaronModConfigManager.get().uiAndVisuals.legacyRevival.oldMessageTrustIndicatorColours ? OLD_MODIFIED_COLOUR : colour;
	}
}

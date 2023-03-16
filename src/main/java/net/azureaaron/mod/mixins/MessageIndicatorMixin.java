package net.azureaaron.mod.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.azureaaron.mod.Config;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;

@Mixin(MessageIndicator.class)
public class MessageIndicatorMixin {
	
	@Shadow @Final private static MessageIndicator NOT_SECURE;
	private static final int AARONMOD$OLD_NOT_SECURE_COLOUR = 15224664;
	private static final int AARONMOD$OLD_MODIFIED_COLOUR = 15386724;

	@Redirect(method = "notSecure", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/MessageIndicator;NOT_SECURE:Lnet/minecraft/client/gui/hud/MessageIndicator;", opcode = Opcodes.GETSTATIC))
	private static MessageIndicator aaronMod$changeNotSecureColour() {
		return (Config.oldMessageIndicatorColours) ? new MessageIndicator(AARONMOD$OLD_NOT_SECURE_COLOUR, NOT_SECURE.icon(), NOT_SECURE.text(), NOT_SECURE.loggedName()) : NOT_SECURE;
	}

	@Redirect(method = "modified", at = @At(value = "NEW", target = "Lnet/minecraft/client/gui/hud/MessageIndicator;"))
	private static MessageIndicator aaronMod$changeModifiedColour(int colour, MessageIndicator.Icon icon, Text text, String logName) {
		return (Config.oldMessageIndicatorColours) ? new MessageIndicator(AARONMOD$OLD_MODIFIED_COLOUR, icon, text, logName) : new MessageIndicator(colour, icon, text, logName);
	}
}

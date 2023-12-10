package net.azureaaron.mod.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.util.Functions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ScreenHandler {

	protected AnvilScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}
	
	@Shadow private String newItemName;
	
	//This also works in vanilla somehow....
	@Inject(method = "setNewItemName", at = @At(value = "FIELD", target = "Lnet/minecraft/screen/AnvilScreenHandler;newItemName:Ljava/lang/String;", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER))
	private void aaronMod$anvilColourCodeNaming() {
		if(AaronModConfigManager.get().colourfulPartyFinderNotes && Functions.isOnHypixel() && Functions.isInSkyblock() && MinecraftClient.getInstance().currentScreen.getTitle().getString().equals("Enter your note!")) this.newItemName = this.newItemName.replace('&', '§');
	}
}

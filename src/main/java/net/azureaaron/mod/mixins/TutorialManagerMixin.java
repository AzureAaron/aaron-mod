package net.azureaaron.mod.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStepHandler;

@Mixin(TutorialManager.class)
public class TutorialManagerMixin {

	@WrapOperation(method = { "createHandler", "setStep" }, at = @At(value = "FIELD", target = "Lnet/minecraft/client/tutorial/TutorialManager;currentHandler:Lnet/minecraft/client/tutorial/TutorialStepHandler;", opcode = Opcodes.PUTFIELD), require = 2)
	private void aaronMod$hideTutorials(TutorialManager manager, TutorialStepHandler stepHandler, Operation<Void> operation) {
		if (AaronModConfigManager.get().hideTutorials) {
			operation.call(manager, null);
		} else {
			operation.call(manager, stepHandler);
		}
	}
}

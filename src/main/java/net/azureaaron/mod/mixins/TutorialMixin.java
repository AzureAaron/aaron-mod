package net.azureaaron.mod.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;

@Mixin(Tutorial.class)
public class TutorialMixin {

	@WrapOperation(method = { "start", "setStep" }, at = @At(value = "FIELD", target = "Lnet/minecraft/client/tutorial/Tutorial;instance:Lnet/minecraft/client/tutorial/TutorialStepInstance;", opcode = Opcodes.PUTFIELD), require = 2)
	private void aaronMod$hideTutorials(Tutorial manager, TutorialStepInstance stepHandler, Operation<Void> operation) {
		if (AaronModConfigManager.get().uiAndVisuals.overlays.hideTutorials) {
			operation.call(manager, null);
		} else {
			operation.call(manager, stepHandler);
		}
	}
}

package net.azureaaron.mod.mixins;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.gui.hud.DebugHud;

@Mixin(DebugHud.class)
public class DebugHudMixin {

	@Inject(method = "getRightText", at = @At("RETURN"), cancellable = true)
	private void aaronMod$addDebugInfo(CallbackInfoReturnable<List<String>> cir) {
		if (AaronModConfigManager.get().extraDebugInfo) {
			List<String> strings = cir.getReturnValue();
			for (int i = 0; i < strings.size(); i++) {
				String str = strings.get(i);

				if (str.startsWith("Java:")) {
					strings.add(i + 4, "Threads: " + ManagementFactory.getThreadMXBean().getThreadCount());
				}

				if (str.startsWith("CPU:")) {
					strings.add(i + 1, "Architecture: " + System.getProperty("os.arch"));
					break;
				}
			}
			return;
		}
	}
}

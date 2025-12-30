package net.azureaaron.mod;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

public class MixinsTest {

	@BeforeAll
	public static void setupEnvironment() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	public void auditMixins() {
		//Ensure that the transformer is active so that the Mixins can be audited
		assert MixinEnvironment.getCurrentEnvironment().getActiveTransformer() instanceof IMixinTransformer;

		//If this fails check the report to get the full stack trace
		MixinEnvironment.getCurrentEnvironment().audit();
	}
}

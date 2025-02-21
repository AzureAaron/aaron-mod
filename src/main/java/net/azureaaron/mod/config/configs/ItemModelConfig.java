package net.azureaaron.mod.config.configs;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.entity.LivingEntity;

public class ItemModelConfig {
	@SerialEntry
	public boolean enableItemModelCustomization = true;

	@SerialEntry
	//This constant is wrongly named in yarn - its actually the swing duration
	public int swingDuration = LivingEntity.GLOWING_FLAG;

	@SerialEntry
	public boolean ignoreMiningEffects = false;

	@SerialEntry
	public AbstractHand mainHand = new AbstractHand();

	@SerialEntry
	public AbstractHand offHand = new AbstractHand();

	public static class AbstractHand {
		@SerialEntry
		public float x = 0f;

		@SerialEntry
		public float y = 0f;

		@SerialEntry
		public float z = 0f;

		@SerialEntry
		public float scale = 1f;

		@SerialEntry
		public float xRotation = 0f;

		@SerialEntry
		public float yRotation = 0f;

		@SerialEntry
		public float zRotation = 0f;
	}
}

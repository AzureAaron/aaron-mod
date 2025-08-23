package net.azureaaron.mod.config.configs;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.azureaaron.mod.annotations.GenEquals;
import net.azureaaron.mod.annotations.GenHashCode;
import net.azureaaron.mod.annotations.GenToString;
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
		public boolean enabled = true;

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

		public AbstractHand copyFrom(AbstractHand other) {
			this.enabled = other.enabled;
			this.x = other.x;
			this.y = other.y;
			this.z = other.z;
			this.scale = other.scale;
			this.xRotation = other.xRotation;
			this.yRotation = other.yRotation;
			this.zRotation = other.zRotation;

			return this;
		}

		@Override
		@GenEquals
		public native boolean equals(Object o);

		@Override
		@GenHashCode
		public native int hashCode();

		@Override
		@GenToString
		public native String toString();
	}
}

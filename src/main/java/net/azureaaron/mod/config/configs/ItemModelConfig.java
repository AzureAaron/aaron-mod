package net.azureaaron.mod.config.configs;

import net.azureaaron.mod.annotations.GenEquals;
import net.azureaaron.mod.annotations.GenHashCode;
import net.azureaaron.mod.annotations.GenToString;
import net.minecraft.entity.LivingEntity;

public class ItemModelConfig {
	public boolean enableItemModelCustomization = true;

	//This constant is wrongly named in yarn - its actually the swing duration
	public int swingDuration = LivingEntity.GLOWING_FLAG;

	public boolean ignoreMiningEffects = false;

	public AbstractHand mainHand = new AbstractHand();

	public AbstractHand offHand = new AbstractHand();

	public static class AbstractHand {
		public boolean enabled = true;

		public float x = 0f;

		public float y = 0f;

		public float z = 0f;

		public float scale = 1f;

		public float xRotation = 0f;

		public float yRotation = 0f;

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

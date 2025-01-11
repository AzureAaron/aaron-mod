package net.azureaaron.mod.config;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import net.minecraft.text.Text;

public class ConfigUtils {

	public static BooleanControllerBuilder createBooleanController(Option<Boolean> opt) {
		return BooleanControllerBuilder.create(opt).coloured(true);
	}

	/**
	 * Workaround for YACL formatting to just 1 decimal place.
	 */
	public static FloatFieldControllerBuilder createFloatFieldController(Option<Float> opt) {
		return FloatFieldControllerBuilder.create(opt).formatValue(f -> Text.of(f.toString()));
	}

	public static FloatFieldControllerBuilder createFloatMultFieldController(Option<Float> opt) {
		return FloatFieldControllerBuilder.create(opt).formatValue(f -> Text.of(f + "x"));
	}

	public static FloatFieldControllerBuilder createFloatDegreesFieldController(Option<Float> opt) {
		return FloatFieldControllerBuilder.create(opt).formatValue(f -> Text.of(f + "°"));
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> EnumControllerBuilder<E> createEnumController(Option<E> opt) {
		Class<E> enumClass = (Class<E>) opt.pendingValue().getClass();

		return EnumControllerBuilder.create(opt).enumClass(enumClass).formatValue(c -> Text.of(c.toString()));
	}
}

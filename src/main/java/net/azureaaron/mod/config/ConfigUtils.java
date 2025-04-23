package net.azureaaron.mod.config;

import java.util.function.UnaryOperator;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import net.minecraft.text.Text;

public class ConfigUtils {

	public static BooleanControllerBuilder createBooleanController(Option<Boolean> opt) {
		return BooleanControllerBuilder.create(opt).coloured(true);
	}

	public static IntegerFieldControllerBuilder createIntPercentageFieldController(Option<Integer> opt, UnaryOperator<IntegerFieldControllerBuilder> controllerUpdater) {
		IntegerFieldControllerBuilder builder = IntegerFieldControllerBuilder.create(opt).formatValue(i -> Text.of(i + "%"));
		controllerUpdater.apply(builder);

		return builder;
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

	public static FloatSliderControllerBuilder createFloatSliderController(Option<Float> opt, UnaryOperator<FloatSliderControllerBuilder> controllerUpdater) {
		FloatSliderControllerBuilder builder = FloatSliderControllerBuilder.create(opt).formatValue(f -> Text.of(f.toString()));
		builder = controllerUpdater.apply(builder);

		return builder;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> EnumControllerBuilder<E> createEnumController(Option<E> opt) {
		Class<E> enumClass = (Class<E>) opt.pendingValue().getClass();

		return EnumControllerBuilder.create(opt).enumClass(enumClass).formatValue(c -> Text.of(c.toString()));
	}
}

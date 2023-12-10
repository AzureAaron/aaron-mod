package net.azureaaron.mod.config;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import net.minecraft.text.Text;

public class ConfigUtils {

	public static BooleanControllerBuilder createBooleanController(Option<Boolean> opt) {
		return BooleanControllerBuilder.create(opt).coloured(true);
	}

	public static FloatFieldControllerBuilder createFloatMultFieldController(Option<Float> opt) {
		return FloatFieldControllerBuilder.create(opt).formatValue(f -> Text.of(f + "x"));
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<?>> CyclingListControllerBuilder<E> createEnumController(Option<E> opt) {
		E[] constants = (E[]) opt.binding().defaultValue().getClass().getEnumConstants();
		
		return CyclingListControllerBuilder.create(opt).values(constants).formatValue(c -> Text.of(c.toString()));
	}
}

package net.azureaaron.mod.config;

import net.azureaaron.dandelion.systems.controllers.BooleanController;
import net.azureaaron.dandelion.systems.controllers.EnumController;

public class ConfigUtils {

	public static BooleanController createBooleanController() {
		return BooleanController.createBuilder().coloured(true).build();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> EnumController<T> createEnumController() {
		return (EnumController<T>) EnumController.createBuilder().build();
	}
}

package net.azureaaron.mod.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that this field member is a configuration entry that will be serialized.
 * @author Aaron
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigEntry {
	/**
	 * Used to differentiate between enum configuration values.
	 * @return Whether the field represents an enum or not.
	 */
	boolean isEnum() default false;
}

/**
 * 
 */
package net.azureaaron.mod.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(SOURCE)
@Target(METHOD)
/**
 * Indicates that the annotated method contains code mostly from vanilla.
 */
public @interface VanillaBased {
	Class<?> value() default void.class;
}

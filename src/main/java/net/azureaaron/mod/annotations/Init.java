package net.azureaaron.mod.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
/**
 * Annotating a method with this will cause the method to be called upon mod initialization.
 * Initializer methods must be public, static, have no arguments, and have a return type of void.
 */
public @interface Init {
	/**
	 * The priority of the initializer method. Lower values will cause the initializer to run sooner
	 * while higher values will result in it being called later.
	 */
	int priority() default 0;
}

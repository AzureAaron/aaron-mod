package net.azureaaron.mod.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(SOURCE)
@Target(TYPE)
/**
 * Indicates that an interface type is used for interface injection
 */
public @interface InterfaceInjected {

}
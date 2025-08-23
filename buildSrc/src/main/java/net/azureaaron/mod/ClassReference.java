package net.azureaaron.mod;

/**
 * Record representing a reference to some class.
 *
 * @param className  The class' name according to {@link Class#getName()} where '.' are replaced with '/' (e.g. 'net.azureaaron.mod.Main').
 * @param descriptor The class' descriptor (e.g. 'Lnet/azureaaron/mod/Main;').
 */
public record ClassReference(String className, String descriptor) {

	public ClassReference(String className) {
		this(className, "L" + className + ";");
	}
}

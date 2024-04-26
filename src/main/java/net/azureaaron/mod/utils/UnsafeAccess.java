package net.azureaaron.mod.utils;

import java.lang.reflect.Field;

import net.azureaaron.mod.commands.ReflectCommand;
import sun.misc.Unsafe;

/**
 * Interface used to gain access to the JVM's {@link Unsafe} instance.<br>
 * Used by the {@link ReflectCommand}.<br><br>
 * 
 * Here there be dragons!
 * 
 * @author Aaron
 */
public interface UnsafeAccess {

	Unsafe UNSAFE = unsafeAdventure();
	
	private static Unsafe unsafeAdventure() {
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			return (Unsafe) theUnsafe.get(null);
		} catch (ReflectiveOperationException e) {
			throw new UnsupportedOperationException("Unable to access Unsafe instance.");
		}
	}
}

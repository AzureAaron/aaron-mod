package net.azureaaron.mod.debug;

import net.fabricmc.loader.api.FabricLoader;

public class Debug {
	/**
	 * Useful for enabling debug mode in production
	 */
	private static final boolean DEBUG_ENABLED = Boolean.parseBoolean(System.getProperty("aaronmod.debug", "false"));

	/**
	 * Returns whether debug mode is enabled or not whether via the JVM flag or by being in a development environment.
	 */
	public static boolean debugEnabled() {
		return DEBUG_ENABLED || FabricLoader.getInstance().isDevelopmentEnvironment();
	}
}

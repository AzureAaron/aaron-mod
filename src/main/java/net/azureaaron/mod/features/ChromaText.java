package net.azureaaron.mod.features;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class ChromaText {
	private static final Minecraft CLIENT = Minecraft.getInstance();
	public static final Identifier ID = Main.id("chroma_text");

	@Init
	public static void init() {
		PackActivationType activationType = AaronModConfigManager.get().uiAndVisuals.chromaText.canDisableChromaPack ? PackActivationType.DEFAULT_ENABLED : PackActivationType.ALWAYS_ENABLED;
		ResourceLoader.registerBuiltinPack(ID, Main.MOD_CONTAINER, activationType);
	}

	/**
	 * Note that this does not guarantee that there isn't another pack enabled on top which overrides
	 * the necessary shaders required for it to work.
	 *
	 * For accurate results, this method should only be called after the client has loaded resource packs.
	 *
	 * @return whether the chroma text pack is enabled
	 */
	public static boolean chromaColourAvailable() {
		try {
			for (String id : CLIENT.getResourcePackRepository().getSelectedIds()) {
				if (id.equals(ID.toString())) {
					return true;
				}
			}
		} catch (Throwable ignored) {}

		return false;
	}
}

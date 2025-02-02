package net.azureaaron.mod.features;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class ChromaText {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	public static final Identifier ID = Identifier.of(Main.NAMESPACE, "chroma_text");

	@Init
	public static void init() {
		ResourceManagerHelper.registerBuiltinResourcePack(ID, Main.MOD_CONTAINER, ResourcePackActivationType.DEFAULT_ENABLED);
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
			for (String id : CLIENT.getResourcePackManager().getEnabledIds()) {
				if (id.equals(ID.toString())) {
					return true;
				}
			}
		} catch (Throwable ignored) {}

		return false;
	}
}

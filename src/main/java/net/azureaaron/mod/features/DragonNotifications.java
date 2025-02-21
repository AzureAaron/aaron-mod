package net.azureaaron.mod.features;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Constants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class DragonNotifications {

	/**
	 * Called from the {@link DragonTimers} class to avoid duplicating logic
	 */
	protected static void notifySpawn(Dragons dragon) {
		if (AaronModConfigManager.get().skyblock.m7.dragonSpawnNotifications) {
			RenderSystem.recordRenderCall(() -> {
				MinecraftClient client = MinecraftClient.getInstance();

				if (client.player == null) return; //Shouldn't be the case ever

				InGameHud hud = client.inGameHud;
				Text text = Text.literal(dragon.name).withColor(dragon.colour);

				hud.clearTitle();
				hud.setDefaultTitleFade();
				hud.setTitle(text);

				client.player.sendMessage(Constants.PREFIX.get().append(Text.literal("The ")).append(text).append(Text.literal(" dragon will spawn soon!")), false);
				client.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 100f, 0.1f);
			});
		}
	}
}

package net.azureaaron.mod.features;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class DragonNotifications {

	/**
	 * Called from the {@link DragonTimers} class to avoid duplicating logic
	 */
	protected static void notifySpawn(Dragons dragon) {
		if (AaronModConfigManager.get().skyblock.m7.dragonSpawnNotifications) {
			RenderSystem.assertOnRenderThread();
			Minecraft client = Minecraft.getInstance();

			if (client.player == null) return; //Shouldn't be the case ever

			Gui hud = client.gui;
			Component text = Component.literal(dragon.name).withColor(dragon.colour);

			hud.clearTitles();
			hud.resetTitleTimes();
			hud.setTitle(text);

			client.player.displayClientMessage(Constants.PREFIX.get().append(Component.literal("The ")).append(text).append(Component.literal(" dragon will spawn soon!")), false);
			client.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 100f, 0.1f);
		}
	}
}

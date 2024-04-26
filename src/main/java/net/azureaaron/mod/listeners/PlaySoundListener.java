package net.azureaaron.mod.listeners;

import java.util.HashSet;

import net.azureaaron.mod.events.PlaySoundEvent;
import net.azureaaron.mod.utils.Cache;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class PlaySoundListener {
	private static final HashSet<net.minecraft.sound.SoundEvent> WARNING_SOUNDS = Util.make(new HashSet<net.minecraft.sound.SoundEvent>(), warningSounds -> {
        warningSounds.add(SoundEvents.ENTITY_WARDEN_NEARBY_CLOSE);
        warningSounds.add(SoundEvents.ENTITY_WARDEN_NEARBY_CLOSER);
        warningSounds.add(SoundEvents.ENTITY_WARDEN_NEARBY_CLOSEST);
        warningSounds.add(SoundEvents.ENTITY_WARDEN_LISTENING_ANGRY);
    });

	public static void listen() {
		PlaySoundEvent.EVENT.register((packet) -> {
			if(packet.getCategory() == SoundCategory.HOSTILE && WARNING_SOUNDS.contains(packet.getSound().value())) {
				Identifier id = Registries.SOUND_EVENT.getId(packet.getSound().value());
				Cache.lastShriekTime = System.currentTimeMillis();
				
				switch(id.toString()) {
				case "minecraft:entity.warden.nearby_close":
					Cache.warningLevel = 1;
					break;
					
				case "minecraft:entity.warden.nearby_closer":
					Cache.warningLevel = 2;
					break;
					
				case "minecraft:entity.warden.nearby_closest":
					Cache.warningLevel = 3;
					break;
					
				case "minecraft:entity.warden.listening_angry":
					Cache.warningLevel = 4;
					break;
				}
			}
		});
	}
}

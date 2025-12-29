package net.azureaaron.mod.skyblock.entity;

import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import com.mojang.logging.LogUtils;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.features.Dragons;
import net.azureaaron.mod.utils.Cache;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class M7DragonGlowAdder extends MobGlowAdder {
	@SuppressWarnings("unused")
	private static final M7DragonGlowAdder INSTANCE = new M7DragonGlowAdder();
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String POWER_TEX = "http://textures.minecraft.net/texture/c20ef06dd60499766ac8ce15d2bea41d2813fe55718864b52dc41cbaae1ea913";
	private static final String FLAME_TEX = "http://textures.minecraft.net/texture/aace6bb3aa4ccac031168202f6d4532597bcac6351059abd9d10b28610493aeb";
	private static final String APEX_TEX = "http://textures.minecraft.net/texture/816f0073c58703d8d41e55e0a3abb042b73f8c105bc41c2f02ffe33f0383cf0a";
	private static final String ICE_TEX = "http://textures.minecraft.net/texture/e4e71671db5f69d2c46a0d72766b249c1236d726782c00a0e22668df5772d4b9";
	private static final String SOUL_TEX = "http://textures.minecraft.net/texture/cad8cc982786fb4d40b0b6e64a41f0d9736f9c26affb898f4a7faea88ccf8997";

	private M7DragonGlowAdder() {}

	@Init
	public static void init() {}

	@Override
	public int computeColour(Entity entity) {
		if (entity instanceof EnderDragonEntity) {
			List<ArmorStandEntity> armourStands = MobGlow.getNearbyArmourStands(entity);

			for (ArmorStandEntity armourStand : armourStands) {
				ItemStack headStack = armourStand.getEquippedStack(EquipmentSlot.HEAD);

				if (headStack.isOf(Items.PLAYER_HEAD)) {
					ProfileComponent profile = headStack.get(DataComponentTypes.PROFILE);
					String textureUrl = getHeadTexUrl(profile);

					return switch (textureUrl) {
						case POWER_TEX -> Dragons.POWER.colour;
						case FLAME_TEX -> Dragons.FLAME.colour;
						case APEX_TEX -> Dragons.APEX.colour;
						case ICE_TEX -> Dragons.ICE.colour;
						case SOUL_TEX -> Dragons.SOUL.colour;
						case null, default -> NO_GLOW;
					};
				}
			}
		}

		return NO_GLOW;
	}

	private static @Nullable String getHeadTexUrl(@Nullable ProfileComponent profile) {
		if (profile != null) {
			Collection<Property> properties = profile.getGameProfile().properties().get("textures");

			for (Property property : properties) {
				try {
					String decoded = new String(Base64.getDecoder().decode(property.value()));
					JsonObject texObj = JsonParser.parseString(decoded).getAsJsonObject();
					String tex = texObj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

					return tex;
				} catch (Exception e) {
					LOGGER.error("[Aaron's Mod Dragon Glow Adder] Failed to decode json.", e);
					return null;
				}
			}
		}

		return null;
	}

	@Override
	public boolean isEnabled() {
		return AaronModConfigManager.get().skyblock.m7.glowingDragons && Cache.inM7Phase5;
	}
}

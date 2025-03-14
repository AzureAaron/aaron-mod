package net.azureaaron.mod.utils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.mojang.util.UndashedUuid;

import net.azureaaron.mod.utils.Http.ApiResponse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.dynamic.Codecs;

public class ApiUtils {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final LoadingCache<String, ProfileResult> GAME_PROFILE_CACHE = CacheBuilder.newBuilder()
			.expireAfterWrite(20, TimeUnit.MINUTES)
			.build(new CacheLoader<>() {
				@Override
				public ProfileResult load(String key) throws Exception {
					return getProfileInternal(key, 0);
				}
			});

	/**
	 * Fetches the given user's {@link GameProfile}.
	 * 
	 * @param nameOrUuid The user's name or {@link UUID} in string format.
	 * 
	 * @return The user's {@link GameProfile} with their name and id only, or {@code null} if the lookup failed.
	 */
	@Nullable
	public static GameProfile getProfile(String nameOrUuid) {
		return GAME_PROFILE_CACHE.getUnchecked(nameOrUuid.replace("-", "")).profile();
	}

	/**
	 * @see #getProfile(String)
	 */
	@Nullable
	public static GameProfile getProfile(UUID uuid) {
		return GAME_PROFILE_CACHE.getUnchecked(UndashedUuid.toString(uuid)).profile();
	}

	private static ProfileResult getProfileInternal(String key, int retries) {
		boolean isUuid = Functions.isUuid(key);

		//Return the game profile for the client to avoid an unnecessary lookup
		if (CLIENT.getSession().getUsername().equalsIgnoreCase(key) || UndashedUuid.toString(CLIENT.getSession().getUuidOrNull()).equalsIgnoreCase(key)) {
			return new ProfileResult(CLIENT.getGameProfile());
		}

		try {
			ApiResponse response = isUuid ? Http.sendUuidToNameRequest(key) : Http.sendNameToUuidRequest(key);

			if (response.ok()) {
				GameProfile profile = Codecs.GAME_PROFILE_WITH_PROPERTIES.parse(JsonOps.INSTANCE, JsonParser.parseString(response.content())).getOrThrow();

				return new ProfileResult(profile);
			} else if (response.ratelimited() && retries < 3) {
				Thread.sleep(800);

				return getProfileInternal(key, ++retries);
			}
		} catch (Exception e) {
			LOGGER.error("[Aaron's Mod Api Utils] Failed to lookup the GameProfile for the key {}.", key, e);
		}

		return new ProfileResult(null);
	}
}

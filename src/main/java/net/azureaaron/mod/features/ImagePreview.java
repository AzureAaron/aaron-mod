package net.azureaaron.mod.features;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.ReceiveChatMessageEvent;
import net.azureaaron.mod.utils.Http;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ImagePreview {
	private static final Logger LOGGER = LogUtils.getLogger();
	//https:\/\/.*\.(?:png|jpg|jpeg)\.?(?:\?.+)?
	//TODO add tests for this regex
	private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("https:\\/\\/.*\\.(?:png|jpg|jpeg)\\.?(?:\\?.+)?", Pattern.CASE_INSENSITIVE);
	private static final ImmutableSet<String> EXPECTED_CONTENT_TYPES = ImmutableSet.of("image/png", "image/jpeg");
	private static final AtomicInteger COUNTER = new AtomicInteger();
	private static final Map<String, CachedImage> IMAGE_CACHE = new ConcurrentHashMap<>();

	@Init
	public static void init() {
		ReceiveChatMessageEvent.EVENT.register(ImagePreview::inspectMessageForImageLinks);
		ScreenEvents.AFTER_INIT.register((client, screen, _scaledWidth, _scaledHeight) -> {
			if (screen instanceof ChatScreen && AaronModConfigManager.get().uiAndVisuals.imagePreview.enableImagePreview) {
				ScreenEvents.afterRender(screen).register((screen1, context, mouseX, mouseY, _delta) -> {
					afterScreenRendered(client, screen1, context, mouseX, mouseY);
				});
			}
		});
	}

	private static void inspectMessageForImageLinks(Text text, boolean overlay, boolean cancelled) {
		if (AaronModConfigManager.get().uiAndVisuals.imagePreview.enableImagePreview && !overlay && !cancelled) {
			ObjectOpenHashSet<String> foundImages = new ObjectOpenHashSet<>();

			//Visit the text to find all image links inside of its click events
			text.visit((style, stringified) -> {
				ClickEvent clickEvent = style.getClickEvent();

				if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
					String url = fixupLink(clickEvent.getValue());

					if (IMAGE_URL_PATTERN.matcher(url).matches()) foundImages.add(url);
				}

				return Optional.empty();
			}, Style.EMPTY);

			for (String imageUrl : foundImages) {
				if (!IMAGE_CACHE.containsKey(imageUrl)) {
					cacheImage(imageUrl);
				}
			}
		}
	}

	private static void cacheImage(String url) {
		CompletableFuture.runAsync(() -> {
			try {
				URI uri = URI.create(url);

				if (isAllowedImageHost(uri.getHost())) {
					//Insert a temporary value to ensure we don't try to cache this twice - ConcurrentHashMaps don't support nulls
					IMAGE_CACHE.put(url, CachedImage.EMPTY);

					MinecraftClient client = MinecraftClient.getInstance();
					InputStream inputStream = Http.sendGenericH2Request(uri, EXPECTED_CONTENT_TYPES);
					NativeImage image = NativeImage.read(inputStream);
					Identifier id = Identifier.of(Main.NAMESPACE, "image_preview/" + COUNTER.getAndIncrement());

					//Schedule the image to be uploaded as a texture on the render thread to avoid Mojang's
					//horribly dangerous system for handling off-thread GL operations
					client.send(() -> {
						client.getTextureManager().registerTexture(id, new NativeImageBackedTexture(image));
						IMAGE_CACHE.put(url, new CachedImage(System.currentTimeMillis(), id, image.getWidth(), image.getHeight()));
					});
				}
			} catch (Exception e) {
				LOGGER.error("[Aaron's Mod Image Preivew] Failed to cache image! URL: {}", url, e);
			}
		});
	}

	private static void afterScreenRendered(MinecraftClient client, Screen screen, DrawContext context, int mouseX, int mouseY) {
		if (!AaronModConfigManager.get().uiAndVisuals.imagePreview.enableImagePreview) return;

		Style style = client.inGameHud.getChatHud().getTextStyleAt(mouseX, mouseY);

		if (style != null && style.getClickEvent() != null) {
			ClickEvent clickEvent = style.getClickEvent();

			if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
				CachedImage image = ImagePreview.IMAGE_CACHE.getOrDefault(fixupLink(clickEvent.getValue()), null);

				if (image != null && image != CachedImage.EMPTY) {
					MatrixStack matrices = context.getMatrices();
					int width = image.width();
					int height = image.height();

					//Scales the image to fit in a 16:9 ratio while ensuring that it can't get bigger
					float scale = Math.min(1f, Math.min(getPrevWidthDiv() / width, getPrevHeightDiv() / height));

					matrices.push();
					matrices.scale(scale, scale, 1f); //The 1f is needed otherwise it'll render behind the chat (the chat's z is scaled by 1 too)
					matrices.translate(0f, 0f, 200f);

					context.drawTexture(RenderLayer::getGuiTextured, image.texture(), 0, 0, 0, 0, width, height, width, height);

					matrices.pop();
				}
			}
		}
	}

	/**
	 * Clears the image cache and frees memory associated with the images
	 */
	public static void clearCache(MinecraftClient client) {
		for (Map.Entry<String, CachedImage> entry : IMAGE_CACHE.entrySet()) {
			client.getTextureManager().destroyTexture(entry.getValue().texture());
		}

		IMAGE_CACHE.clear();
	}

	private static boolean isAllowedImageHost(String host) {
		return host.equals("cdn.discordapp.com") || host.equals("media.discordapp.net") || host.equals("i.imgur.com");
	}

	/**
	 * Converts imgur.com to i.imgur.com
	 */
	private static String fixupLink(String url) {
		if (url.startsWith("https://imgur.com")) return url.replace("https://imgur.com", "https://i.imgur.com");

		return url;
	}

	private static float getPrevWidthDiv() {
		return (16 * 16f) * AaronModConfigManager.get().uiAndVisuals.imagePreview.scale;
	}

	private static float getPrevHeightDiv() {
		return (9f * 16f) * AaronModConfigManager.get().uiAndVisuals.imagePreview.scale;
	}

	private static record CachedImage(long creationTime, Identifier texture, int width, int height) {
		private static final CachedImage EMPTY = new CachedImage(0, null, 0, 0);
	}
}

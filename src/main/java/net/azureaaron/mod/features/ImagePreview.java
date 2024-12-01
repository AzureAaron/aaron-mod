package net.azureaaron.mod.features;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.ReceiveChatMessageEvent;
import net.azureaaron.mod.utils.Http;
import net.azureaaron.mod.utils.TextTransformer;
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
	private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("(https?:\\/\\/.*\\.(?:png|jpg|jpeg|gif)\\.?(?:\\?.+)?)", Pattern.CASE_INSENSITIVE);
	private static final ImmutableSet<String> EXPECTED_CONTENT_TYPES = ImmutableSet.of("image/png", "image/jpeg");

	private static final AtomicInteger COUNTER = new AtomicInteger();
	//The actual image caches, we retain a separate set of urls to avoid attempting to cache the same image twice which'd cause a memory leak
	private static final Object2ObjectOpenHashMap<String, CachedImage> IMAGE_CACHE = new Object2ObjectOpenHashMap<>();
	private static final ObjectOpenHashSet<String> IMAGE_URLS_CACHED = new ObjectOpenHashSet<>();

	public static void init() {
		ReceiveChatMessageEvent.EVENT.register(ImagePreview::inspectMessageForImageLinks);
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof ChatScreen && AaronModConfigManager.get().imagePreview) {
				ScreenEvents.afterRender(screen).register((screen1, context, mouseX, mouseY, delta) -> {
					afterScreenRendered(screen1, context, mouseX, mouseY);
				});
			}
		});
	}

	private static void inspectMessageForImageLinks(Text text, boolean overlay, boolean cancelled) {
		if (AaronModConfigManager.get().imagePreview && !overlay && !cancelled) {
			ObjectOpenHashSet<String> foundImages = new ObjectOpenHashSet<>();
			Text deconstructedText = TextTransformer.deconstructAllComponents(text); 
			List<Text> components = deconstructedText.getSiblings();

			for (Text currentComponent : components) {
				Style currentStyle = currentComponent.getStyle();
				ClickEvent clickEvent = currentStyle.getClickEvent();

				if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
					String url = fixupLink(clickEvent.getValue());

					if (IMAGE_URL_PATTERN.matcher(url).matches()) foundImages.add(url);
				}
			}

			for (String imageUrl: foundImages) {
				if (!IMAGE_URLS_CACHED.contains(imageUrl)) {
					cacheImage(imageUrl);
				}
			}
		}
	}

	private static void cacheImage(String url) {
		CompletableFuture.runAsync(() -> {
			MinecraftClient client = MinecraftClient.getInstance();
			URI uri = URI.create(url);

			if (isAllowedImageHost(uri.getHost())) {
				IMAGE_URLS_CACHED.add(url);

				try {
					InputStream inputStream = Http.sendGenericH2Request(uri, EXPECTED_CONTENT_TYPES);
					NativeImage image = NativeImage.read(inputStream);
					Identifier id = Identifier.of(Main.NAMESPACE, "image_preview/" + COUNTER.getAndIncrement());

					client.getTextureManager().registerTexture(id, new NativeImageBackedTexture(image));

					IMAGE_CACHE.put(url, new CachedImage(System.currentTimeMillis(), id, image.getWidth(), image.getHeight()));
				} catch (Exception e) {
					Main.LOGGER.error("[Aaron's Mod Image Preivew] Failed to cache image! URL: {}", url, e);

					IMAGE_URLS_CACHED.remove(url);
				}
			}
		});
	}

	private static void afterScreenRendered(Screen screen, DrawContext context, int mouseX, int mouseY) {
		MinecraftClient client = MinecraftClient.getInstance();
		Style style = client.inGameHud.getChatHud().getTextStyleAt(mouseX, mouseY);

		if (style != null && style.getClickEvent() != null) {
			ClickEvent clickEvent = style.getClickEvent();

			if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
				CachedImage image = ImagePreview.IMAGE_CACHE.getOrDefault(fixupLink(clickEvent.getValue()), null);

				if (image != null) {
					MatrixStack matrices = context.getMatrices();
					int width = image.width();
					int height = image.height();

					//Reasonably scale the image so it doesn't go off screen and isn't too large or too small
					float scale = Math.min(1f, Math.min(getPrevHeightDiv() / height, getPrevWidthDiv() / width));

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
		IMAGE_URLS_CACHED.clear();
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

	private static float getPrevHeightDiv() {
		return (6.75f * 15f) * AaronModConfigManager.get().imagePreviewScale;
	}

	private static float getPrevWidthDiv() {
		return (12f * 15f) * AaronModConfigManager.get().imagePreviewScale;
	}

	private static record CachedImage(long creationTime, Identifier texture, int width, int height) {}
}

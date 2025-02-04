package net.azureaaron.mod.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import net.azureaaron.mod.Main;

/**
 * The name speaks for itself.
 * 
 * @author Aaron
 */
public class Http {
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(10))
			.followRedirects(Redirect.NORMAL)
			.build();
	private static final String HYPIXEL_PROXY = "https://api.azureaaron.net/hypixel/v2/";
	private static final String NAME_TO_UUID = "https://api.minecraftservices.com/minecraft/profile/lookup/name/";
	private static final String UUID_TO_NAME = "https://api.minecraftservices.com/minecraft/profile/lookup/";
	private static final String USER_AGENT = "Aaron's Mod/" + Main.MOD_VERSION + " (" + Main.MINECRAFT_VERSION + ")";

	private static ApiResponse sendGetRequestInternal(String url, @Nullable String apiToken) throws IOException, InterruptedException {
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
				.GET()
				.header("Accept", "application/json")
				.header("Accept-Encoding", "gzip, deflate")
				.header("User-Agent", USER_AGENT)
				.version(Version.HTTP_2)
				.uri(URI.create(url));

		if (apiToken != null) requestBuilder.header("Token", apiToken);

		HttpResponse<InputStream> response = HTTP_CLIENT.send(requestBuilder.build(), BodyHandlers.ofInputStream());
		InputStream decodedInputStream = getDecodedInputStream(response);

		String body = new String(decodedInputStream.readAllBytes());
		decodedInputStream.close();

		return new ApiResponse(body, response.statusCode(), url, response.headers());
	}

	public static String sendGetRequest(@NotNull String url) throws IOException, InterruptedException {
		return sendGetRequestInternal(url, null).content();
	}

	public static String sendHypixelRequest(@NotNull String endpoint, @NotNull String parameters) throws IOException, InterruptedException, ApiException {
		ApiResponse response = sendGetRequestInternal(HYPIXEL_PROXY + endpoint + parameters, ApiAuthentication.getToken());
		response.tryThrow();

		return response.content();
	}

	public static ApiResponse sendNameToUuidRequest(@NotNull String name) throws IOException, InterruptedException, ApiException {
		return sendGetRequestInternal(NAME_TO_UUID + name, null);
	}

	public static ApiResponse sendUuidToNameRequest(@NotNull String uuid) throws IOException, InterruptedException, ApiException {
		return sendGetRequestInternal(UUID_TO_NAME + uuid, null);
	}

	public static String sendPostRequest(@NotNull String url, @NotNull String requestBody, @NotNull String contentType) throws IOException, InterruptedException {		
		HttpRequest request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header("Accept", contentType)
				.header("Accept-Encoding", "gzip, deflate")
				.header("Content-Type", contentType)
				.header("User-Agent", USER_AGENT)
				.uri(URI.create(url))
				.build();

		HttpResponse<InputStream> response = HTTP_CLIENT.send(request, BodyHandlers.ofInputStream());
		InputStream decodedInputStream = getDecodedInputStream(response);

		String body = new String(decodedInputStream.readAllBytes());
		decodedInputStream.close();

		return body;
	}

	public static InputStream sendGenericH2Request(URI uri, ImmutableSet<String> expectedContentTypes) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.header("Accept", "*/*")
				.header("Accept-Encoding", "gzip, deflate")
				.header("User-Agent", USER_AGENT)
				.version(Version.HTTP_2)
				.uri(uri)
				.build();
		
		HttpResponse<InputStream> response = HTTP_CLIENT.send(request, BodyHandlers.ofInputStream());
		int statusCode = response.statusCode();
		String contentType = getContentType(response);
		
		//Status code & content type enforcement
		if (statusCode != 200) throw new IllegalStateException("Request was unsuccessful! Code: " + statusCode);
		if (!expectedContentTypes.contains(contentType)) throw new IllegalStateException("Unexpected content type received! Content Type: " + contentType);
		
		return getDecodedInputStream(response);
	}
	
	private static InputStream getDecodedInputStream(HttpResponse<InputStream> response) {
		String encoding = getContentEncoding(response);

		try {
			return switch (encoding) {
				case "" -> response.body();
				case "gzip" -> new GZIPInputStream(response.body());
				case "deflate" -> new InflaterInputStream(response.body());
				default -> throw new UnsupportedOperationException("The server sent content in unexpected encoding: " + encoding);
			};
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private static String getContentEncoding(HttpResponse<InputStream> response) {
		return response.headers().firstValue("Content-Encoding").orElse("");
	}
	
	private static String getContentType(HttpResponse<InputStream> response) {
		return response.headers().firstValue("Content-Type").orElse("");
	}

	//TODO give this a more generic name?
	public record ApiResponse(String content, int statusCode, String url, HttpHeaders headers) {
		
		public boolean ok() {
			return statusCode == 200;
		}
		
		public boolean ratelimited() {
			return statusCode == 429;
		}
		
		public ApiException createException() {
			return new ApiException("[Aaron's Mod] API Error! URL: " + url + ", Code: " + statusCode + ", Body: " + content);
		}

		public void tryThrow() throws ApiException {
			if (!ok()) throw createException();
		}
	}

	//FIXME deprecate maybe?
	public static class ApiException extends Exception {
		@Serial
		private static final long serialVersionUID = 2804124614055383667L;

		public ApiException(String errorMessage) {
			super(errorMessage);
		}
	}
}

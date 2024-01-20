package net.azureaaron.mod.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableSet;

import net.azureaaron.mod.Main;

/**
 * The name speaks for itself.
 * 
 * @author Aaron
 */
public class Http {
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
	private static final String HYPIXEL_BASE = "https://api.hypixel.net/";
	private static final String AARON_BASE = "https://api.azureaaron.net/hypixel/";
	private static final String NAME_TO_UUID = "https://api.minecraftservices.com/minecraft/profile/lookup/name/";
	private static final String UUID_TO_NAME = "https://api.minecraftservices.com/minecraft/profile/lookup/";
	private static final String NETWORTH = "https://maro.skyblockextras.com/api/networth/categories";
	private static final String MOULBERRY = "https://moulberry.codes/";
	private static final String USER_AGENT = "Aaron's Mod/" + Main.MOD_VERSION;
	
	private static ApiResponse sendGetRequest(String url, boolean throwOnNonOk) throws IOException, InterruptedException, ApiException {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.header("Accept", "application/json")
				.header("Accept-Encoding", "gzip, deflate")
				.header("User-Agent", USER_AGENT)
				.version(Version.HTTP_2)
				.uri(URI.create(url))
				.build();
		
		HttpResponse<InputStream> response = HTTP_CLIENT.send(request, BodyHandlers.ofInputStream());
		InputStream decodedInputStream = getDecodedInputStream(response);
		
		String body = new String(decodedInputStream.readAllBytes());
		int statusCode = response.statusCode();
		
		if (throwOnNonOk && statusCode != 200) throw new ApiException("[Aaron's Mod] API Error! URL: " + url + ", Code: " + statusCode + ", Body: " + body);
		
		return new ApiResponse(body, statusCode, url, response.headers());
	}
			
	public static String sendUnauthorizedHypixelRequest(String endpoint, @NotNull String parameters) throws IOException, InterruptedException, ApiException {
		return sendGetRequest(HYPIXEL_BASE + endpoint + parameters, true).content();
	}
	
	public static String sendAuthorizedHypixelRequest(String endpoint, @NotNull String parameters) throws IOException, InterruptedException, ApiException {
		return sendGetRequest(AARON_BASE + endpoint + parameters, true).content();
	}
	
	public static ApiResponse sendNameToUuidRequest(String name) throws IOException, InterruptedException, ApiException {
		return sendGetRequest(NAME_TO_UUID + name, false);
	}
	
	public static ApiResponse sendUuidToNameRequest(String uuid) throws IOException, InterruptedException, ApiException {
		return sendGetRequest(UUID_TO_NAME + uuid, false);
	}
	
	public static String sendMoulberryRequest(String endpoint) throws IOException, InterruptedException, ApiException {
		return sendGetRequest(MOULBERRY + endpoint, false).content();
	}
	
	public static String sendNetworthRequest(String body) throws IOException, InterruptedException {		
		HttpRequest request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.header("Accept", "application/json")
				.header("Content-Type", "application/json") //We don't want them to know this isn't SBE!
				.uri(URI.create(NETWORTH))
				.build();
		
		HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
		
		return response.body();
	}
	
	public static InputStream sendGenericH2Request(URI uri, ImmutableSet<String> expectedContentTypes) throws IOException, InterruptedException, ApiException {		
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
			switch (encoding) {
				case "":
					return response.body();
				case "gzip":
					return new GZIPInputStream(response.body());
				case "deflate":
					return new InflaterInputStream(response.body());
				default:
					throw new UnsupportedOperationException("The server sent content in unexpected encoding: " + encoding);
			}
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
	}
	
	public static class ApiException extends Exception {
		private static final long serialVersionUID = 2804124614055383667L;

		public ApiException(String errorMessage) {
			super(errorMessage);
		}
	}
}

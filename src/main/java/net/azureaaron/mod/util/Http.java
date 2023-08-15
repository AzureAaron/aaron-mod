package net.azureaaron.mod.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.jetbrains.annotations.NotNull;

import net.azureaaron.mod.Main;

/**
 * The name speaks for itself.
 * 
 * @author Aaron
 */
public class Http {
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();
	private static final String HYPIXEL_BASE = "https://api.hypixel.net/";
	private static final String AARON_BASE = "https://api.azureaaron.net/hypixel/";
	private static final String NAME_TO_UUID = "https://api.minecraftservices.com/minecraft/profile/lookup/name/";
	private static final String NETWORTH = "https://maro.skyblockextras.com/api/networth/categories";
	private static final String MOULBERRY = "https://moulberry.codes/";
	private static final String USER_AGENT = "Aaron's Mod/" + Main.MOD_VERSION;
			
	public static String sendUnauthorizedHypixelRequest(@NotNull String endpoint, @NotNull final String parameters) throws IOException, InterruptedException, ApiException {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.header("Accept", "application/json")
				.header("Accept-Encoding", "gzip, deflate")
				.header("User-Agent", USER_AGENT)
				.version(Version.HTTP_2)
				.uri(URI.create(HYPIXEL_BASE + endpoint + parameters))
				.build();
		
		HttpResponse<InputStream> response = HTTP_CLIENT.send(request, BodyHandlers.ofInputStream());
		if(response.statusCode() != 200) throw new ApiException("Hypixel Api Error [code=" + response.statusCode() + ", body=\"" + response.body() + "\"]");

		InputStream decodedInputStream = getDecodedInputStream(response);
		String apiResponse = new String(decodedInputStream.readAllBytes());
		
		return apiResponse;
	}
	
	public static String sendAuthorizedHypixelRequest(@NotNull String endpoint, @NotNull final String parameters) throws IOException, InterruptedException, ApiException {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.header("Accept", "application/json")
				.header("Accept-Encoding", "gzip, deflate")
				.header("User-Agent", USER_AGENT)
				.version(Version.HTTP_2)
				.uri(URI.create(AARON_BASE + endpoint + parameters))
				.build();
		
		HttpResponse<InputStream> response = HTTP_CLIENT.send(request, BodyHandlers.ofInputStream());
		if(response.statusCode() != 200) throw new ApiException("Hypixel Authorized Api Proxy Error [code=" + response.statusCode() + ", body=\"" + response.body() + "\"]");

		InputStream decodedInputStream = getDecodedInputStream(response);
		String apiResponse = new String(decodedInputStream.readAllBytes());
		
		return apiResponse;
	}
	
	public static String sendNameToUuidRequest(@NotNull final String name) throws IOException, InterruptedException, ApiException {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.header("Accept", "application/json")
				.header("User-Agent", USER_AGENT)
				.version(Version.HTTP_2)
				.uri(URI.create(NAME_TO_UUID + name))
				.build();
		
		HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
		if(response.statusCode() != 200) throw new ApiException("Minecraft Services Api Error [code=" + response.statusCode() + ", body=\"" + response.body() + "\"]");
		return response.body();
	}
	
	public static String sendNetworthRequest(@NotNull final String body) throws IOException, InterruptedException {		
		HttpRequest request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.header("Accept", "application/json")
				.header("Content-Type", "application/json") //We don't want them to know this isn't SBE!
				.uri(URI.create(NETWORTH))
				.build();
		
		HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
		return response.body();
	}
	
	public static String sendMoulberryRequest(@NotNull final String endpoint) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.header("Accept", "application/json")
				.header("Accept-Encoding", "gzip, deflate")
				.header("User-Agent", USER_AGENT)
				.version(Version.HTTP_2)
				.uri(URI.create(MOULBERRY + endpoint))
				.build();
		
		HttpResponse<InputStream> response = HTTP_CLIENT.send(request, BodyHandlers.ofInputStream());
		InputStream decodedInputStream = getDecodedInputStream(response);
		String apiResponse = new String(decodedInputStream.readAllBytes());
		
		return apiResponse;
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
	
	public static class ApiException extends Exception {
		private static final long serialVersionUID = 2804124614055383667L;

		public ApiException(String errorMessage) {
			super(errorMessage);
		}
	}
}

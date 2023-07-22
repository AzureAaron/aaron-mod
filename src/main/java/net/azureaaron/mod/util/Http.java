package net.azureaaron.mod.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.jetbrains.annotations.NotNull;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.Main;

/**
 * The name speaks for itself.
 * 
 * @author Aaron
 */
public class Http {
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();
	private static final String HYPIXEL_BASE = "https://api.hypixel.net/";
	private static final String NAME_TO_UUID = "https://api.minecraftservices.com/minecraft/profile/lookup/name/";
	private static final String NETWORTH = "https://maro.skyblockextras.com/api/networth/categories";
	private static final String MOULBERRY = "https://moulberry.codes/";
	private static final String USER_AGENT = "Aaron's Mod/" + Main.MOD_VERSION;
			
	public static String sendHypixelRequest(@NotNull String endpoint, @NotNull final String parameters, boolean authorization) throws IOException, InterruptedException, ApiException {
		if(authorization == true) endpoint += "?key=" + Config.key;
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.header("Accept", "application/json")
				.header("User-Agent", USER_AGENT)
				.version(Version.HTTP_2)
				.uri(URI.create(HYPIXEL_BASE + endpoint + parameters))
				.build();
		
		HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
		if(response.statusCode() != 200) throw new ApiException("Hypixel Api Error [code=" + response.statusCode() + ", body=\"" + response.body() + "\"]");
		return response.body();
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
				.header("User-Agent", USER_AGENT)
				.version(Version.HTTP_2)
				.uri(URI.create(MOULBERRY + endpoint))
				.build();
		
		HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
		return response.body();
	}
	
	public static class ApiException extends Exception {
		private static final long serialVersionUID = 2804124614055383667L;

		public ApiException(String errorMessage) {
			super(errorMessage);
		}
	}
}

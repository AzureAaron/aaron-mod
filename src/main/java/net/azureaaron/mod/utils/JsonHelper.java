package net.azureaaron.mod.utils;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Helper methods to assist in retrieving values nested in JSON objects.
 *
 * All methods are fully null safe, whether it be from passing a {@code null} root object or from encountering a nonexistent or null object/value.
 *
 * @implNote While this provides null safety, it does not provide type safety. The caller must know the target type of the target JSON element.
 */
public class JsonHelper {

	public static OptionalInt getInt(JsonObject root, String path) {
		return getElement(root, path, JsonElement::getAsInt).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public static OptionalLong getLong(JsonObject root, String path) {
		return getElement(root, path, JsonElement::getAsLong).map(OptionalLong::of).orElseGet(OptionalLong::empty);
	}

	public static Optional<Float> getFloat(JsonObject root, String path) {
		return getElement(root, path, JsonElement::getAsFloat);
	}

	public static OptionalDouble getDouble(JsonObject root, String path) {
		return getElement(root, path, JsonElement::getAsDouble).map(OptionalDouble::of).orElseGet(OptionalDouble::empty);
	}

	public static Optional<Boolean> getBoolean(JsonObject root, String path) {
		return getElement(root, path, JsonElement::getAsBoolean);
	}

	public static Optional<String> getString(JsonObject root, String path) {
		return getElement(root, path, JsonElement::getAsString);
	}

	public static Optional<JsonObject> getObject(JsonObject root, String path) {
		return getElement(root, path, JsonElement::getAsJsonObject);
	}

	public static Optional<JsonArray> getArray(JsonObject root, String path) {
		return getElement(root, path, JsonElement::getAsJsonArray);
	}

	private static <T> Optional<T> getElement(JsonObject root, String path, ValueExtractor<T> valueExtractor) {
		//If root is null
		if (root == null) return Optional.empty();

		//Fast path for if we just want the field itself
		if (!path.contains(".")) {
			return root.has(path) && !root.get(path).isJsonNull() ? Optional.of(valueExtractor.to(root.get(path))) : Optional.empty();
		}

		String[] split = path.split("\\.");
		String propertyName = split[split.length - 1];
		String[] objects2Traverse = new String[split.length - 1];

		//Get the traversal path
		System.arraycopy(split, 0, objects2Traverse, 0, split.length - 1);

		JsonObject currentLevel = root;

		for (String objectName : objects2Traverse) {
			if (currentLevel.has(objectName) && !currentLevel.get(objectName).isJsonNull()) {
				currentLevel = currentLevel.getAsJsonObject(objectName);
			} else {
				return Optional.empty();
			}
		}

		return currentLevel.has(propertyName) && !currentLevel.get(propertyName).isJsonNull() ? Optional.of(valueExtractor.to(currentLevel.get(propertyName))) : Optional.empty();
	}

	@FunctionalInterface
	private interface ValueExtractor<R> {
		R to(JsonElement element);
	}

	/**
	 * Clears all null values from a {@link JsonElement} as a workaround for JsonOps not handling JsonNulls correctly.
	 *
	 * Credit to ResourcefulLib for the original implementation this was based off
	 */
	public static JsonElement clearNullValues(JsonElement rootElement) {
		switch (rootElement) {
			case JsonObject object -> {
				JsonObject newObject = new JsonObject();

				for (String key : object.keySet()) {
					JsonElement element = clearNullValues(object.get(key));

					if (element != null) newObject.add(key, element);
				}

				return newObject;
			}

			case JsonArray array -> {
				JsonArray newArray = new JsonArray();

				for (JsonElement item : array) {
					JsonElement element = clearNullValues(item);

					if (element != null) newArray.add(element);
				}

				return newArray;
			}

			case null -> {
				return null;
			}

			default -> {
				return rootElement.isJsonNull() ? null : rootElement;
			}
		}
	}
}

package me.manuloff.apps.knasu.study.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * @author Manuloff
 * @since 15:44 10.12.2024
 */
@UtilityClass
public class GsonUtil {

	public static final Gson gson = new Gson();

	public static JsonElement fromJson(@NonNull String json) {
		return gson.fromJson(json, JsonElement.class);
	}

	public static String toJson(@NonNull JsonElement json) {
		return gson.toJson(json);
	}

	@NonNull
	public static JsonObject createObject(@NonNull Object... objects) {
		return appendObject(new JsonObject(), objects);
	}

	public static JsonObject appendObject(@NonNull JsonObject object, @NonNull Object... objects) {
		if (objects.length % 2 != 0) {
			throw new IllegalArgumentException("Number of objects must be even");
		}

		for (int i = 0; i < objects.length; i += 2) {
			String key = (String) objects[i];

			object.add(key, gson.toJsonTree(objects[i + 1]));
		}

		return object;
	}

	@NonNull
	public static JsonArray array(@NonNull Object... objects) {
		JsonArray array = new JsonArray();

		for (@NonNull Object object : objects) {
			array.add(gson.toJsonTree(object));
		}

		return array;
	}

}

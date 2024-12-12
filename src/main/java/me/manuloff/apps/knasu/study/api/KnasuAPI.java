package me.manuloff.apps.knasu.study.api;

import lombok.Locked;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.manuloff.apps.knasu.study.api.response.GroupsResponse;
import me.manuloff.apps.knasu.study.api.response.ScheduleResponse;
import me.manuloff.apps.knasu.study.api.response.TeachersResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Manuloff
 * @since 00:59 11.12.2024
 */
@UtilityClass
public class KnasuAPI {

	private static final Map<String, CacheEntry> CACHE_STORAGE = new HashMap<>();

	@SneakyThrows
	private static <T> T get(@NonNull String url, @NonNull Function<Document, T> function, long cacheLifeTime) {
		CacheEntry entry = CACHE_STORAGE.get(url);

		if (entry == null || entry.isExpired(cacheLifeTime)) {
			T response = function.apply(Jsoup.parse(new URL(url), 10_000));

			CACHE_STORAGE.put(url, new CacheEntry(response, System.currentTimeMillis()));

			return response;
		}

		return (T) entry.getCachedResponse();
	}

	//

	@NonNull
	@Locked.Read
	public static GroupsResponse getGroups() {
		return get("https://knastu.ru/students/schedule", GroupsResponse::new, TimeUnit.DAYS.toMillis(1));
	}

	@NonNull
	@Locked.Read
	public static TeachersResponse getTeachers() {
		return get("https://knastu.ru/teachers/schedule", TeachersResponse::new, TimeUnit.DAYS.toMillis(1));
	}

	@NonNull
	@Locked.Read
	public static ScheduleResponse getGroupSchedule(@NonNull UUID groupId, @NonNull String day) {
		return get("https://knastu.ru/students/schedule/" + groupId + "?day=" + day, ScheduleResponse::new, TimeUnit.HOURS.toMillis(1));
	}

	@NonNull
	@Locked.Read
	public static ScheduleResponse getTeacherSchedule(@NonNull String teacherId, @NonNull String day) {
		return get("https://knastu.ru/teachers/schedule/" + teacherId + "?day=" + day, ScheduleResponse::new, TimeUnit.HOURS.toMillis(1));
	}

}

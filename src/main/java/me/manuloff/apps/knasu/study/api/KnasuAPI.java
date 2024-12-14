package me.manuloff.apps.knasu.study.api;

import lombok.Locked;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.manuloff.apps.knasu.study.api.response.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Manuloff
 * @since 00:59 11.12.2024
 */
@UtilityClass
public class KnasuAPI {

	private static final Map<String, CacheEntry> CACHE_STORAGE = new HashMap<>();

	@SneakyThrows
	private static <T> T parseHtml(@NonNull String url, @NonNull Function<Document, T> function, long cacheLifeTime) {
		return parse(url, () -> {
			try {
				return function.apply(Jsoup.parse(new URL(encodeUrl(url)), 10_000));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, cacheLifeTime);
	}

	@SneakyThrows
	private static <T> T parsePdf(@NonNull String url, @NonNull Function<PDDocument, T> function, long cacheLifeTime) {
		return parse(url, () -> {
			try (InputStream in = new URL(encodeUrl(url)).openConnection().getInputStream()) {
				try (PDDocument document = PDDocument.load(in)) {
					return function.apply(document);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, cacheLifeTime);
	}

	@SneakyThrows
	private static <T> T parse(@NonNull String url, @NonNull Supplier<T> supplier, long cacheLifeTime) {
		CacheEntry entry = CACHE_STORAGE.get(url);

		if (entry == null || entry.isExpired(cacheLifeTime)) {
			T response = supplier.get();

			CACHE_STORAGE.put(url, new CacheEntry(response, System.currentTimeMillis()));

			return response;
		}

		return (T) entry.getCachedResponse();
	}

	@NonNull
	private static String encodeUrl(@NonNull String url) {
		String[] parts = url.split("\\?");
		if (parts.length < 2) {
			return url;
		}

		String baseUrl = parts[0];
		String[] parameters = parts[1].split("&");

		StringBuilder encodedParams = new StringBuilder();
		for (String param : parameters) {
			String[] keyValue = param.split("=");
			if (keyValue.length == 2) {

				String encodedValue = URLEncoder.encode(keyValue[1], StandardCharsets.UTF_8);
				encodedParams.append(keyValue[0]).append("=").append(encodedValue).append("&");
			}
		}

		if (!encodedParams.isEmpty()) {
			encodedParams.deleteCharAt(encodedParams.length() - 1);
		}

		return baseUrl + "?" + encodedParams;
	}

	//

	@NonNull
	@Locked.Read
	public static GroupsResponse getGroups() {
		return parseHtml("https://knastu.ru/students/schedule", GroupsResponse::new, TimeUnit.DAYS.toMillis(7));
	}

	@NonNull
	@Locked.Read
	public static TeachersResponse getTeachers() {
		return parseHtml("https://knastu.ru/teachers/schedule", TeachersResponse::new, TimeUnit.DAYS.toMillis(7));
	}

	@NonNull
	@Locked.Read
	public static ScheduleResponse getGroupSchedule(@NonNull UUID groupId, @NonNull String day) {
		return parseHtml("https://knastu.ru/students/schedule/" + groupId + "?day=" + day, ScheduleResponse::new, TimeUnit.HOURS.toMillis(1));
	}

	@NonNull
	@Locked.Read
	public static ScheduleResponse getTeacherSchedule(@NonNull String teacherId, @NonNull String day) {
		return parseHtml("https://knastu.ru/teachers/schedule/" + teacherId + "?day=" + day, ScheduleResponse::new, TimeUnit.HOURS.toMillis(1));
	}

	@NonNull
	@Locked.Read
	public static GroupCodesResponse getGroupCodes() {
		return parseHtml("https://www.knastu.ru/page/1404", GroupCodesResponse::new, TimeUnit.DAYS.toMillis(7));
	}

	@NonNull
	@Locked.Read
	public static EducationalProgramResponse getEducationalProgram(@NonNull String specialityCode) {
		return parseHtml("https://knastu.ru/sveden/education/" + specialityCode, EducationalProgramResponse::new, TimeUnit.DAYS.toMillis(7));
	}

	@NonNull
	@Locked.Read
	public static WorkingStudyPlanResponse getWorkingProgram(@NonNull String href) {
		return parseHtml("https://knastu.ru/sveden/education/" + href, WorkingStudyPlanResponse::new, TimeUnit.DAYS.toMillis(7));
	}

	@NonNull
	@Locked.Read
	public static GroupProgramAnnotation getGroupProgramAnnotation(@NonNull String href) {
		return parsePdf("https://knastu.ru" + href, GroupProgramAnnotation::new, TimeUnit.DAYS.toMillis(7));
	}

	@NonNull
	@Locked.Read
	public static AcademicCalendarResponse getAcademicCalendar(@NonNull String href) {
		return parsePdf("https://knastu.ru" + href, AcademicCalendarResponse::new, TimeUnit.DAYS.toMillis(7));
	}
}

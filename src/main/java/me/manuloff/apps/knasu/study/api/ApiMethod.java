package me.manuloff.apps.knasu.study.api;

import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.response.StudyGroups;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Manuloff
 * @since 21:23 09.12.2024
 */
public final class ApiMethod<T> {

	private static final long CACHE_LIFE_TIME = TimeUnit.MINUTES.toMillis(5);

	//

	public static final ApiMethod<StudyGroups> GET_STUDY_GROUPS = new ApiMethod<>(StudyGroups::new, "https://knastu.ru/students/schedule");

	//

	private final Function<Document, T> function;
	private final String url;

	private T cachedResponse;
	private long lastExecutedTime;

	private ApiMethod(@NonNull Function<Document, T> function, @NonNull String url) {
		this.function = function;
		this.url = url;
	}

	@NonNull
	public T execute() throws IOException {
		long now = System.currentTimeMillis();

		if (this.cachedResponse == null || now - this.lastExecutedTime > CACHE_LIFE_TIME) {
			System.out.println("new!");
			this.cachedResponse = this.function.apply(Jsoup.parse(new URL(this.url), 10_000));
			this.lastExecutedTime = now;
		} else {
			System.out.println("cached!");
		};

		return this.cachedResponse;
	}
}

package me.manuloff.apps.knasu.study.api.response;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Manuloff
 * @since 03:16 17.12.2024
 */
public class FacultiesResponse {

	private final Map<String, String> faculties = new HashMap<>();

	public FacultiesResponse(@NonNull Document document) {
		Element tbody = document.selectFirst("tbody");
		assert tbody != null;

		for (Element row : tbody.select("tr")) {
			Elements columns = row.select("td");
			if (columns.size() != 2) continue;

			Element column = columns.get(1);
			Element element = column.selectFirst("a");
			if (element == null) continue;

			this.faculties.put(element.ownText(), element.attr("href"));
		}
	}

	@Nullable
	public String getUrl(@NonNull String faculty) {
		return this.faculties.get(faculty);
	}

	@NonNull
	public Set<Map.Entry<String, String>> getFaculties() {
		return faculties.entrySet();
	}
}

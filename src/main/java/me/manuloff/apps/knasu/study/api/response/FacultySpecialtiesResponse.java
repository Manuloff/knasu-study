package me.manuloff.apps.knasu.study.api.response;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Manuloff
 * @since 12:05 17.12.2024
 */
public class FacultySpecialtiesResponse {

	private final Map<String, String> departmentByGroupCode = new HashMap<>();

	public FacultySpecialtiesResponse(@NonNull Document document) {
		for (Element element : document.body().select("aside.prog-code")) {
			String code = Objects.requireNonNull(element.selectFirst("strong a")).text();
			String href = Objects.requireNonNull(element.select("a").last()).attr("href");

			this.departmentByGroupCode.put(code, href);
		}

	}

	@Nullable
	public String getDepartmentBySpecialityCode(@NonNull String groupCode) {
		return departmentByGroupCode.get(groupCode);
	}

	@NonNull
	public Set<Map.Entry<String, String>> getDepartments() {
		return departmentByGroupCode.entrySet();
	}
}

package me.manuloff.apps.knasu.study.api.response;

import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Manuloff
 * @since 22:41 12.12.2024
 */
public class EducationalProgramResponse {

	private final Map<EnrollmentInfo, String> workingStudyPlanUrls = new HashMap<>();
	private final Map<EnrollmentInfo, String> academicCalendars = new HashMap<>();

	public EducationalProgramResponse(@NonNull Document document) {
		Element body = document.body().selectFirst("tbody");
		assert body != null;

		String enrollmentYear = null;

		for (Element row : body.select("tr")) {
			if (row.className().equalsIgnoreCase("profile-first-row")) {
				enrollmentYear = Objects.requireNonNull(row.selectFirst("td.profile-year")).text();
				continue;
			}

			Elements columns = row.select("td");
			for (int i = 0; i < columns.size(); i++) {
				Element column = columns.get(i);

				Element aElement = column.selectFirst("a");
				if (aElement == null) continue;

				String text = aElement.text();
				Map<EnrollmentInfo, String> map;
				EducationForm form;

				if (text.equalsIgnoreCase("РПД")) {
					map = this.workingStudyPlanUrls;

					form = switch (i) {
						case 2 -> EducationForm.FULL_TIME;
						case 3 -> EducationForm.BLENDED;
						case 4 -> EducationForm.PART_TIME;
						default -> throw new IllegalStateException("Unexpected value: " + i);
					};
				} else if (text.equalsIgnoreCase("КУГ")) {
					map = this.academicCalendars;

					form = switch (i) {
						case 1 -> EducationForm.FULL_TIME;
						case 2 -> EducationForm.BLENDED;
						case 3 -> EducationForm.PART_TIME;
						default -> throw new IllegalStateException("Unexpected value: " + i);
					};
				} else {
					continue;
				}

				String href = aElement.attr("href");

				map.put(new EnrollmentInfo(enrollmentYear, form), href);
			}
		}
	}

	@NonNull
	public Map<EnrollmentInfo, String> getWorkingStudyPlanUrls() {
		return Map.copyOf(workingStudyPlanUrls);
	}

	@NonNull
	public Map<EnrollmentInfo, String> getAcademicCalendars() {
		return Map.copyOf(academicCalendars);
	}

	@Nullable
	public String getWorkingStudyPlanUrl(@NonNull String groupName) {
		return this.resolveValueByGroupName(groupName, this.workingStudyPlanUrls);
	}

	@Nullable
	public String getAcademicCalendarUrl(@NonNull String groupName) {
		return this.resolveValueByGroupName(groupName, this.academicCalendars);
	}

	@Nullable
	private String resolveValueByGroupName(@NonNull String groupName, @NonNull Map<EnrollmentInfo, String> map) {
		char c = groupName.charAt(0);

		// Найдем год, который заканчивается на первую цифру названия группы
		String enrollmentYear = map.keySet().stream().map(EnrollmentInfo::getEnrollmentYear)
				.filter(year -> year.endsWith(String.valueOf(c))).findFirst().orElse(null);

		EducationForm type;

		if (groupName.contains("а")) { // 0ГУба-1
			type = EducationForm.PART_TIME;

		} else if (groupName.contains("в")) { // 1ГУбв-1
			type = EducationForm.BLENDED;

		} else { // 1ГУб-1
			type = EducationForm.FULL_TIME;
		}

		EnrollmentInfo info = new EnrollmentInfo(enrollmentYear, type);

		return map.get(info);
	}

	@Nullable
	public String getWorkingStudyPlanUrl(@NonNull EnrollmentInfo enrollmentInfo) {
		return this.workingStudyPlanUrls.get(enrollmentInfo);
	}

	@Nullable
	public String getAcademicCalendarUrl(@NonNull EnrollmentInfo enrollmentInfo) {
		return this.academicCalendars.get(enrollmentInfo);
	}

	@Data
	public static class EnrollmentInfo {
		private final String enrollmentYear;
		private final EducationForm educationForm;
	}

	private enum EducationForm {
		FULL_TIME,
		PART_TIME,
		BLENDED
	}
}

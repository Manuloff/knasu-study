package me.manuloff.apps.knasu.study.api.response;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * @author Manuloff
 * @since 21:40 09.12.2024
 */
@ToString
@EqualsAndHashCode
public final class GroupsResponse {

	private final Map<String, Faculty> facultyByName = new LinkedHashMap<>();
	private final Map<String, UUID> groupIdByGroupName = new LinkedHashMap<>();

	public GroupsResponse(@NonNull Document document) {
		for (Element elementFaculty : document.body().select("div.faculty")) {
			String facultyAbbreviation = Objects.requireNonNull(elementFaculty.select("div.faculty a").first()).attr("name");
			String facultyName = Objects.requireNonNull(elementFaculty.select("div.faculty h4").first()).text();

			Map<String, List<String>> map = new LinkedHashMap<>();

			for (Element row : elementFaculty.select("tr")) {
				Elements columns = row.select("td");

				String year = columns.get(0).text().split(" ")[0];

				List<String> groups = new LinkedList<>();
				map.put(year, groups);

				for (Element link : Objects.requireNonNull(columns.get(1).select("td.sectiontableentry1").first()).select("a")) {
					UUID groupId = UUID.fromString(link.attr("href").split("/")[3]);
					String groupName = link.text();

					this.groupIdByGroupName.put(groupName, groupId);
					groups.add(groupName);
				}
			}

			this.facultyByName.put(facultyAbbreviation, new Faculty(facultyAbbreviation, facultyName, map));
		}
	}

	@NonNull
	public List<String> getFaculties() {
		return new ArrayList<>(this.facultyByName.keySet());
	}

	@Nullable
	public Faculty getFacultyBy(@NonNull String facultyName) {
		return this.facultyByName.get(facultyName);
	}

	@Nullable
	public List<String> getEnrollmentYearsBy(@NonNull String facultyName) {
		Faculty faculty = this.facultyByName.getOrDefault(facultyName, null);

		return faculty != null ? new ArrayList<>(faculty.enrollmentYearsGroups.keySet()) : null;
	}

	@Nullable
	public List<String> getGroupsBy(@NonNull String facultyName, @NonNull String enrollmentYear) {
		Faculty faculty = this.getFacultyBy(facultyName);
		if (faculty == null) return null;

		return faculty.enrollmentYearsGroups.getOrDefault(enrollmentYear, null);
	}

	@NonNull
	public List<String> getAllGroups() {
		return new ArrayList<>(this.groupIdByGroupName.keySet());
	}

	@Nullable
	public UUID getGroupIdByGroupName(@NonNull String groupName) {
		return this.groupIdByGroupName.getOrDefault(groupName, null);
	}

	@EqualsAndHashCode
	@ToString
	@RequiredArgsConstructor
	public static class Faculty {
		private final String abbreviation;
		private final String name;

		private final Map<String, List<String>> enrollmentYearsGroups;
	}

}
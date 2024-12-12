package me.manuloff.apps.knasu.study.api.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Manuloff
 * @since 01:47 11.12.2024
 */
public final class TeachersResponse {

	private final List<Teacher> teachers = new LinkedList<>();

	public TeachersResponse(@NonNull Document document) {
		Element content = document.body().select("div.article-content").first();
		assert content != null;

		for (Element teacher : content.select("div.col-md-4 a")) {
			String[] split = teacher.text().split(" ");

			this.teachers.add(new Teacher(split[0], split[1], split[2], teacher.attr("href").split("/")[3]));
		}
	}

	@NonNull
	public List<Teacher> getTeachers() {
		return Collections.unmodifiableList(this.teachers);
	}

	@Nullable
	public Teacher getByFullName(@NonNull String fullName) {
		return this.stream(teacher -> teacher.getFullName().equalsIgnoreCase(fullName)).findFirst().orElse(null);
	}

	@NonNull
	public List<Teacher> getByFirstName(@NonNull String firstName) {
		return this.stream(teacher -> teacher.getFirstName().equalsIgnoreCase(firstName)).collect(Collectors.toList());
	}

	@NonNull
	public List<Teacher> getByLastName(@NonNull String lastName) {
		return this.stream(teacher -> teacher.getLastName().equalsIgnoreCase(lastName)).collect(Collectors.toList());
	}

	@NonNull
	public List<Teacher> getByPatronymic(@NonNull String patronymic) {
		return this.stream(teacher -> teacher.getPatronymic().equalsIgnoreCase(patronymic)).collect(Collectors.toList());
	}

	private Stream<Teacher> stream(@NonNull Predicate<Teacher> predicate) {
		return this.teachers.stream().filter(predicate);
	}

	@NonNull
	public List<Teacher> getByQuery(@NonNull String query) {
		Map<Teacher, Double> similarityMap = new HashMap<>();

		for (Teacher teacher : this.teachers) {
			double similarity = this.calculateSimilarity(teacher.getFullName(), query);
			if (similarity > 30) {
				similarityMap.put(teacher, similarity);
			}
		}

		List<Teacher> list = similarityMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey).collect(Collectors.toList());

		Collections.reverse(list);

		return list;
	}

	private double calculateSimilarity(@NonNull String s1, @NonNull String s2) {
		int maxLength = Math.max(s1.length(), s2.length());
		int distance = this.getLevenshteinDistance(s1, s2);

		return (1.0 - (double) distance / maxLength) * 100;
	}

	private int getLevenshteinDistance(@NonNull String s1, @NonNull String s2) {
		int len1 = s1.length();
		int len2 = s2.length();
		int[][] dp = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++) {
			for (int j = 0; j <= len2; j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
							dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1));
				}
			}
		}
		return dp[len1][len2];
	}

	//

	@Getter
	@RequiredArgsConstructor
	@EqualsAndHashCode
	public static class Teacher {
		private final String lastName;
		private final String firstName;
		private final String patronymic;

		private final String id;

		@NonNull
		public String getFullName() {
			return this.lastName + " " + this.firstName + " " + this.patronymic;
		}

		@NonNull
		public String getInitials() {
			return this.lastName + " " + this.firstName.charAt(0) + ". " + this.patronymic.charAt(0) + ". ";
		}

		@Override
		public String toString() {
			return "Teacher{" + this.getFullName() + "}";
		}
	}
}

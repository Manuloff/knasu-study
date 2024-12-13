package me.manuloff.apps.knasu.study.api.response;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Manuloff
 * @since 01:02 13.12.2024
 */
@Getter
public class WorkingStudyPlanResponse {

	private final List<Period> periods = new LinkedList<>();
	private final String stateExam;

	public WorkingStudyPlanResponse(@NonNull Document document) {
		Element table = document.body().selectFirst("tbody");
		assert table != null;

		Period period = null;
		String stateExam = null;

		for (Element row : table.select("tr")) {
			if (row.className().equalsIgnoreCase("sms-caption")) {
				period = new Period(row.text());
				this.periods.add(period);
				continue;
			}

			if (row.hasAttr("data-type")) {
				Elements columns = row.select("td");

				String programName = columns.get(0).text();

				String annotation = parseHref(columns.get(2));
				String workingProgramDiscipline = parseHref(columns.get(3));

				if (annotation == null || workingProgramDiscipline == null || period == null) continue;

				period.programs.add(new Program(programName, annotation, workingProgramDiscipline));
				continue;
			}

			Elements columns = row.select("td");
			if (columns.get(0).text().equalsIgnoreCase("Государственная итоговая аттестация")) {
				stateExam = this.parseHref(columns.get(2));

				// Это последняя информация, которая нам нужна в таблице, поэтому можно прервать цикл
				break;
			}
		}

		this.stateExam = stateExam;
	}

	@Nullable
	private String parseHref(@NonNull Element column) {
		return column.select("a").stream().filter(element -> !element.select("button").isEmpty())
				.findFirst().map(element -> element.attr("href")).orElse(null);
	}



	@Data
	public static class Period {
		@NonNull
		private final String name;
		@NonNull
		private final List<Program> programs = new LinkedList<>();
	}

	@Data
	public static class Program {
		@NonNull
		private final String name;

		@NonNull
		private final String annotation;
		@NonNull
		private final String workingProgramDiscipline;
	}

}

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

	private final String methodicalMaterials;
	private final String competencyPassport;
	private final String literatureRegistry;
	private final String ebsRegistry;
	private final String softwareRegistry;

	private final List<Period> periods = new LinkedList<>();
	private final String stateExam;

	public WorkingStudyPlanResponse(@NonNull Document document) {
		Element body = document.body();

		String methodicalMaterials = null;
		String competencyPassport = null;
		String literatureRegistry = null;
		String ebsRegistry = null;
		String softwareRegistry = null;

		Element filterSwitch = body.selectFirst("div.btn-group.filter-switch");
		if (filterSwitch != null) {
			for (Element element : filterSwitch.select("span")) {
				if (!element.hasAttr("itemprop")) {
					continue;
				}

				String href = parseHref(element);
				if (href == null) continue;

				switch (element.text()) {
					case "Методические материалы" -> {
						methodicalMaterials = href;
					}
					case "Паспорта компетенций" -> {
						competencyPassport = href;
					}
					case "Реестр литературы" -> {
						literatureRegistry = href;
					}
					case "Реестр ЭБС" -> {
						ebsRegistry = href;
					}
					case "Реестр ПО" -> {
						softwareRegistry = href;
					}
				}
			}
		}

		this.methodicalMaterials = methodicalMaterials;
		this.competencyPassport = competencyPassport;
		this.literatureRegistry = literatureRegistry;
		this.ebsRegistry = ebsRegistry;
		this.softwareRegistry = softwareRegistry;

		Element table = body.selectFirst("tbody");
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

				String assessmentResources = parseHref(columns.get(5));

				period.programs.add(new Program(programName, annotation, workingProgramDiscipline, assessmentResources));
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
		@Nullable
		private final String assessmentResources;
	}

}

package me.manuloff.apps.knasu.study.api.response;

import lombok.*;
import me.manuloff.apps.knasu.study.util.CalendarUtils;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Manuloff
 * @since 14:59 11.12.2024
 */
@Getter
public class ScheduleResponse {

	private final String id;
	private final String date;

	private final List<LessonTime> lessonTimes = new LinkedList<>();
	private final List<DailySchedule> dailySchedules = new LinkedList<>();

	public ScheduleResponse(@NonNull Document document) {
		Element body = document.body();

		String[] urlSplit = document.baseUri().split("/")[5].split("\\?");
		this.id = urlSplit[0];
		this.date = urlSplit[1].replace("day=", "");

		Element table = body.selectFirst("table");
		if (table == null) {
			for (int i = 0; i < 8; i++) {
				this.lessonTimes.add(new LessonTime("", ""));
			}
			for (int i = 0; i < 6; i++) {
				DailySchedule schedule = new DailySchedule(CalendarUtils.removeYearFromDate(CalendarUtils.plusDays(this.date, i)), CalendarUtils.getShortName(DayOfWeek.values()[i]));

				for (int j = 0; j < 8; j++) {
					schedule.lessons.add(null);
				}

				this.dailySchedules.add(schedule);
			}
			return;
		}

		Elements headTable = body.select("thead th");
		for (int i = 1; i < headTable.size(); i++) {
			String[] split = headTable.get(i).text().split(" ");

			this.dailySchedules.add(new DailySchedule(split[1], split[0]));
		}

		for (Element row : body.select("tbody tr")) {
			Elements columns = row.select("td");

			String startEndTime = columns.get(0).text().substring(2);
			String[] split = startEndTime.split(" - ");

			String startTime = split[0];
			String endTime = split[1];

			this.lessonTimes.add(new LessonTime(startTime, endTime));

			for (int i = 1; i < columns.size(); i++) {
				Element column = columns.get(i);

				DailySchedule dailySchedule = this.dailySchedules.get(i - 1);

				// Если этот элемент существует внутри колонки, то пара есть, иначе нет.
				Elements elements = column.select("div.table-print-inner-event.col-sm-12.col-md-12");
				if (elements.isEmpty()) {
					dailySchedule.lessons.add(null);
					continue;
				}

				Lesson lesson = new Lesson(startTime, endTime);
				dailySchedule.lessons.add(lesson);

				for (Element element : elements) {
					element = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(element.parent()).select("b").first()).parent());

					String additionalInfo = element.select("i").text();

					Element subject = element.select("b").first();
					assert subject != null;

					String fullName = subject.attr("title");
					String shortName = subject.text();

					String type = element.textNodes().get(0).text();

					// У расписания учебных групп есть только один участник - учитель,
					// для учителя же может быть несколько групп.
					List<String> participants = new LinkedList<>();
					Element teacherLink = element.selectFirst("a[title=Расписание преподавателя]");
					if (teacherLink != null) {
						participants.add(teacherLink.text());
					} else {
						for (Element groupLink : element.select("a[title=Расписание группы]")) {
							participants.add(groupLink.text());
						}
					}

					Element venueLink = element.selectFirst("a[title=Расписание аудитории]");
					String venue = venueLink != null ? venueLink.text() : "";

					lesson.lessonInfos.add(new LessonInfo(startTime, endTime, additionalInfo.isEmpty() ? null : additionalInfo, shortName, fullName, type, participants, venue));
				}
			}
		}
	}

	@NonNull
	public List<LessonTime> getLessonTimes() {
		return Collections.unmodifiableList(this.lessonTimes);
	}

	@NonNull
	public List<DailySchedule> getDailySchedules() {
		return Collections.unmodifiableList(this.dailySchedules);
	}

	@NonNull
	public DailySchedule getDailyScheduleByDayOfWeek(@NonNull DayOfWeek dayOfWeek) {
		return this.dailySchedules.get(dayOfWeek.ordinal());
	}

	@NonNull
	public DailySchedule getDailyScheduleByDate(@NonNull String date) {
		return this.dailySchedules.stream().filter(schedule -> schedule.date.equals(date)).findFirst().orElse(this.createEmptyDailySchedule(date));
	}

	@NonNull
	private DailySchedule createEmptyDailySchedule(@NonNull String date) {
		DailySchedule schedule = new DailySchedule(date, "");

		for (int i = 0; i < this.lessonTimes.size(); i++) {
			schedule.lessons.add(null);
		}

		return schedule;
	}

	@Data
	public static class DailySchedule {
		@NonNull
		private final String date;
		@NonNull
		private final String dayOfWeek;

		@NonNull
		private final List<Lesson> lessons = new LinkedList<>();
	}

	@ToString
	@EqualsAndHashCode(callSuper = true)
	public static class Lesson extends LessonTime {

		@NonNull
		private final List<LessonInfo> lessonInfos = new LinkedList<>();

		public Lesson(@NonNull String startTime, @NonNull String endTime) {
			super(startTime, endTime);
		}

		public boolean isParallelLesson() {
			return this.lessonInfos.size() > 1;
		}

		@NonNull
		public LessonInfo getFirst() {
			return this.get(0);
		}

		@NonNull
		public LessonInfo get(int index) {
			return this.lessonInfos.get(index);
		}

		@NonNull
		public List<LessonInfo> getLessons() {
			return Collections.unmodifiableList(this.lessonInfos);
		}
	}

	@Getter
	@ToString
	@EqualsAndHashCode(callSuper = true)
	public static class LessonInfo extends LessonTime {

		@Nullable
		private final String additionalInfo;
		@NonNull
		private final String shortName;
		@NonNull
		private final String fullName;
		@NonNull
		private final String type;
		@NonNull
		private final List<String> participants;
		@NonNull
		private final String venue;

		public LessonInfo(@NonNull String startTime,
						  @NonNull String endTime,
						  @Nullable String additionalInfo,
						  @NonNull String shortName,
						  @NonNull String fullName,
						  @NonNull String type,
						  @NonNull List<String> participants,
						  @NonNull String venue) {
			super(startTime, endTime);
			this.additionalInfo = additionalInfo;
			this.shortName = shortName;
			this.fullName = fullName;
			this.type = type;
			this.participants = participants;
			this.venue = venue;
		}
	}


	@Data
	public static class LessonTime {
		@NonNull
		private final String startTime;
		@NonNull
		private final String endTime;
	}

}

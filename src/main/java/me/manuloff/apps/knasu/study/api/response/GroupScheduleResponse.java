package me.manuloff.apps.knasu.study.api.response;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.DayOfWeek;
import java.util.*;

/**
 * @author Manuloff
 * @since 14:59 11.12.2024
 */
@Getter
public class GroupScheduleResponse {

	private final String groupName;
	private final UUID groupId;

	private final List<LessonTime> lessonTimes = new LinkedList<>();
	private final List<DailySchedule> dailySchedules = new LinkedList<>();

	public GroupScheduleResponse(@NonNull Document document) {
		Element body = document.body();

		this.groupId = UUID.fromString(document.baseUri().split("/")[5].split("\\?")[0]);

		// Если на странице существует название группы, то расписание существует,
		// иначе его попросту нет на этой неделе
		Element groupNameElement = body.select("h2.schedule-simple-3-hide").first();
		if (groupNameElement == null) {
			this.groupName = "";
			return;
		}

		this.groupName = groupNameElement.text();

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
				Element element = column.selectFirst("div.table-print-inner-event.col-sm-12.col-md-12");
				if (element == null) {
					dailySchedule.lessons.add(null);
					continue;
				}

				String additionalInfo = element.select("i").text();

				Element subject = element.select("b").first();
				assert subject != null;

				String fullName = subject.attr("title");
				String shortName = subject.text();

				String type = element.textNodes().get(0).text();

				String teacher = Objects.requireNonNull(element.selectFirst("a")).text();

				String venue = element.select("b").get(1).text();

				dailySchedule.lessons.add(new Lesson(startTime, endTime, additionalInfo.isEmpty() ? null : additionalInfo, shortName, fullName, type, teacher, venue));
			}
		}
	}

	public boolean isOk() {
		return this.groupName != null;
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

	@Nullable
	public DailySchedule getDailyScheduleByDate(@NonNull String date) {
		return this.dailySchedules.stream().filter(schedule -> schedule.date.equals(date)).findFirst().orElse(null);
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

	@Getter
	public static class Lesson extends LessonTime {

		public Lesson(@NonNull String startTime,
					  @NonNull String endTime,
					  @Nullable String additionalInfo,
					  @NonNull String shortName,
					  @NonNull String fullName,
					  @NonNull String type,
					  @NonNull String teacher,
					  @NonNull String location) {
			super(startTime, endTime);
			this.additionalInfo = additionalInfo;
			this.shortName = shortName;
			this.fullName = fullName;
			this.type = type;
			this.teacher = teacher;
			this.location = location;
		}

		@Nullable
		private final String additionalInfo;

		@NonNull
		private final String shortName;
		@NonNull
		private final String fullName;
		@NonNull
		private final String type;
		@NonNull
		private final String teacher;
		@NonNull
		private final String location;
	}


	@Data
	public static class LessonTime {
		@NonNull
		private final String startTime;
		@NonNull
		private final String endTime;
	}

}

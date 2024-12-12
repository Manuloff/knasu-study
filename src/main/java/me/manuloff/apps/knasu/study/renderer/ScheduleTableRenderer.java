package me.manuloff.apps.knasu.study.renderer;

import com.suke.jtable.Rect;
import com.suke.jtable.TextAlign;
import com.suke.jtable.graphics.FontStyle;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.response.ScheduleResponse;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * @author Manuloff
 * @since 22:28 11.12.2024
 */
public final class ScheduleTableRenderer extends AbstractTableRenderer {

	private ScheduleTableRenderer(@NonNull ScheduleResponse groupSchedule, @Nullable String date) {
		this.table.setFontName("Roboto")
				.setCellPadding(new Rect(20, 0))
				.setFontSize(18)
				.setTextAlign(TextAlign.CENTER);

		for (int i = 0; i < 4; i++) {
			this.table.getRow(i).setCellBackground(new Color(0xE9E9E9))
					.setFontStyle(FontStyle.BOLD)
					.setFontSize(22);
		}

		int lastLesson = 0;

		if (date != null) {
			ScheduleResponse.DailySchedule dailySchedule = groupSchedule.getDailyScheduleByDate(date);
			assert dailySchedule != null;

			lastLesson = this.getLastLesson(dailySchedule);
		} else {
			for (ScheduleResponse.DailySchedule schedule : groupSchedule.getDailySchedules()) {
				int ll = this.getLastLesson(schedule);

				if (lastLesson < ll) {
					lastLesson = ll;
				}
			}
		}

		System.out.println("lastLesson = " + lastLesson);

		this.drawTimings(groupSchedule.getLessonTimes(), lastLesson);
		this.nextColumn();

		if (date != null) {
			ScheduleResponse.DailySchedule dailySchedule = groupSchedule.getDailyScheduleByDate(date);
			assert dailySchedule != null;

			this.drawDailySchedule(dailySchedule, lastLesson);
		} else {
			for (ScheduleResponse.DailySchedule schedule : groupSchedule.getDailySchedules()) {
				this.drawDailySchedule(schedule, lastLesson);
			}
		}
	}

	private int getLastLesson(@NonNull ScheduleResponse.DailySchedule schedule) {
		List<ScheduleResponse.Lesson> lessons = schedule.getLessons();
		if (lessons.isEmpty()) return 0;

		for (int i = lessons.size() - 1; i >= 0; i--) {
			if (lessons.get(i) != null) return i + 1;
		}

		return 0;
	}

	private void drawTimings(@NonNull List<ScheduleResponse.LessonTime> lessonTimes, int lastLesson) {
		this.cell();
		this.cell().setText("№");
		this.cell().setText("пары");
		this.cell();

		for (int i = 0; i < lastLesson; i++) {
			this.drawTiming(i + 1, lessonTimes.get(i));
		}
	}

	private void drawTiming(int i, @NonNull ScheduleResponse.LessonTime lessonTime) {
		this.cell();
		this.cell().setText(String.valueOf(i));
		this.cell().setText(lessonTime.getStartTime() + " -").setFontStyle(FontStyle.BOLD);
		this.cell().setText(lessonTime.getEndTime()).setFontStyle(FontStyle.BOLD);
		this.cell();
		this.cell();
	}

	private void drawDailySchedule(@NonNull ScheduleResponse.DailySchedule schedule, int lastLesson) {
		List<ScheduleResponse.Lesson> lessons = schedule.getLessons();

		int maxLessons = lessons.stream().filter(Objects::nonNull).mapToInt(lesson -> lesson.getLessons().size()).max().orElse(1);
		System.out.println(schedule.getDate() + " - " + maxLessons);

		this.cell(maxLessons);
		this.cell(maxLessons).setText(schedule.getDayOfWeek());
		this.cell(maxLessons).setText(schedule.getDate());
		this.cell(maxLessons);

		for (int i = 0; i < lastLesson; i++) {
			ScheduleResponse.Lesson lesson = lessons.get(i);
			if (lesson == null) {
				this.drawLesson(null, maxLessons);
				continue;
			}

			if (lesson.isParallelLesson()) {
				int rememberedCol = this.currentCol;
				int rememberedRow = this.currentRow;

				for (ScheduleResponse.LessonInfo lessonInfo : lesson.getLessons()) {
					this.currentRow = rememberedRow;

					this.drawLesson(lessonInfo, 1);

					this.currentCol++;
				}

				this.currentCol = rememberedCol;

			} else {
				this.drawLesson(lesson.getFirst(), maxLessons);
			}
		}

		this.currentCol += maxLessons;
		this.currentRow = 0;
	}

	private void drawLesson(@Nullable ScheduleResponse.LessonInfo lesson, int colSpan) {
		if (lesson != null) {
			if (lesson.getAdditionalInfo() != null) {
				this.cell(colSpan).setText(lesson.getAdditionalInfo()).setFontStyle(FontStyle.ITALIC).setFontColor(Color.RED);
			} else {
				this.cell(colSpan);
			}

			this.cell(colSpan).setText(lesson.getShortName()).setFontStyle(FontStyle.BOLD);
			this.cell(colSpan).setText(lesson.getType());
			this.cell(colSpan).setText(String.join(" ", lesson.getParticipants())).setFontColor(new Color(0x3EB7EF));
			this.cell(colSpan).setText(lesson.getVenue()).setFontStyle(FontStyle.BOLD);
			this.cell(colSpan);

		} else {
			this.cell(colSpan);
			this.cell(colSpan);
			this.cell(colSpan);
			this.cell(colSpan);
			this.cell(colSpan);
			this.cell(colSpan);
		}
	}

	public static byte[] render(@NonNull ScheduleResponse groupSchedule, @Nullable String date) {
		return new ScheduleTableRenderer(groupSchedule, date).render();
	}

}

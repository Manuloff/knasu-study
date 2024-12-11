package me.manuloff.apps.knasu.study.util;

import com.suke.jtable.Cell;
import com.suke.jtable.Rect;
import com.suke.jtable.Table;
import com.suke.jtable.TextAlign;
import com.suke.jtable.graphics.FontStyle;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.response.GroupScheduleResponse;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Manuloff
 * @since 22:28 11.12.2024
 */
public final class GroupScheduleTableRenderer {

	private final Table table;

	private int currentRow;
	private int currentCol;

	private GroupScheduleTableRenderer(@NonNull GroupScheduleResponse groupSchedule, @Nullable String date) {
		this.table = new Table();

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
			GroupScheduleResponse.DailySchedule dailySchedule = groupSchedule.getDailyScheduleByDate(date);
			assert dailySchedule != null;

			lastLesson = this.getLastLesson(dailySchedule);
		} else {
			for (GroupScheduleResponse.DailySchedule schedule : groupSchedule.getDailySchedules()) {
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
			GroupScheduleResponse.DailySchedule dailySchedule = groupSchedule.getDailyScheduleByDate(date);
			assert dailySchedule != null;

			this.drawDailySchedule(dailySchedule, lastLesson);
		} else {
			for (GroupScheduleResponse.DailySchedule schedule : groupSchedule.getDailySchedules()) {
				this.drawDailySchedule(schedule, lastLesson);
				this.nextColumn();
			}
		}
	}

	private int getLastLesson(@NonNull GroupScheduleResponse.DailySchedule schedule) {
		List<GroupScheduleResponse.Lesson> lessons = schedule.getLessons();
		if (lessons.isEmpty()) return 0;

		for (int i = lessons.size() - 1; i >= 0; i--) {
			if (lessons.get(i) != null) return i + 1;
		}

		return 0;
	}

	private void drawTimings(@NonNull List<GroupScheduleResponse.LessonTime> lessonTimes, int lastLesson) {
		this.cell();
		this.cell().setText("№");
		this.cell().setText("пары");
		this.cell();

		for (int i = 0; i < lastLesson; i++) {
			this.drawTiming(i + 1, lessonTimes.get(i));
		}
	}

	private void drawTiming(int i, @NonNull GroupScheduleResponse.LessonTime lessonTime) {
		this.cell();
		this.cell().setText(String.valueOf(i));
		this.cell().setText(lessonTime.getStartTime() + " -").setFontStyle(FontStyle.BOLD);
		this.cell().setText(lessonTime.getEndTime()).setFontStyle(FontStyle.BOLD);
		this.cell();
		this.cell();
	}

	private void drawDailySchedule(@NonNull GroupScheduleResponse.DailySchedule schedule, int lastLesson) {
		this.cell();
		this.cell().setText(schedule.getDayOfWeek());
		this.cell().setText(schedule.getDate());
		this.cell();

		List<GroupScheduleResponse.Lesson> lessons = schedule.getLessons();

		for (int i = 0; i < lastLesson; i++) {
			this.drawLesson(lessons.get(i));
		}
	}

	private void drawLesson(@Nullable GroupScheduleResponse.Lesson lesson) {
		if (lesson != null) {
			if (lesson.getAdditionalInfo() != null) {
				this.cell().setText(lesson.getAdditionalInfo()).setFontStyle(FontStyle.ITALIC).setFontColor(Color.RED);
			} else {
				this.cell();
			}

			this.cell().setText(lesson.getShortName()).setFontStyle(FontStyle.BOLD);
			this.cell().setText(lesson.getType());
			this.cell().setText(lesson.getTeacher()).setFontColor(new Color(0x3EB7EF));
			this.cell().setText(lesson.getLocation()).setFontStyle(FontStyle.BOLD);
			this.cell();

		} else {
			this.cell();
			this.cell();
			this.cell();
			this.cell();
			this.cell();
			this.cell();
		}
	}

	private Cell cell() {
		return this.table.getCell(this.currentRow++, this.currentCol);
	}

	private void nextColumn() {
		this.currentCol++;
		this.currentRow = 0;
	}

	public static byte[] render(@NonNull GroupScheduleResponse groupSchedule, @Nullable String date) {
		GroupScheduleTableRenderer renderer = new GroupScheduleTableRenderer(groupSchedule, date);

		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			renderer.table.savePng(os);
			return os.toByteArray();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

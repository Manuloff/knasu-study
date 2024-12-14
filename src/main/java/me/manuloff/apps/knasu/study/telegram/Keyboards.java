package me.manuloff.apps.knasu.study.telegram;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.manuloff.apps.knasu.study.util.CalendarUtils;
import me.manuloff.apps.knasu.study.util.DataEntry;
import me.manuloff.apps.knasu.study.util.InlineKeyboardBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Manuloff
 * @since 00:07 11.12.2024
 */
@UtilityClass
public class Keyboards {

	@NonNull
	public static InlineKeyboardMarkup facultiesKeyboard(@NonNull List<String> faculties) {
		List<InlineKeyboardButton> buttons = new LinkedList<>();

		for (String faculty : faculties) {
			buttons.add(new InlineKeyboardButton(faculty)
					.callbackData(DataEntry.of(0, faculty).asString())
			);
		}

		return InlineKeyboardBuilder.pretty(buttons, 3);
	}

	@NonNull
	public static InlineKeyboardMarkup enrollmentYearsKeyboard(@NonNull DataEntry entry, @NonNull List<String> enrollmentYears) {
		List<InlineKeyboardButton> buttons = new LinkedList<>();
		for (String year : enrollmentYears) {
			buttons.add(new InlineKeyboardButton(year)
					.callbackData(entry.deepCopy().set(0, 1).add(year).asString())
			);
		}

		return InlineKeyboardBuilder.pretty(buttons, 3);
	}

	@NonNull
	public static InlineKeyboardMarkup groupsKeyboard(@NonNull List<String> groups) {
		List<InlineKeyboardButton> buttons = new LinkedList<>();

		for (String group : groups) {
			buttons.add(new InlineKeyboardButton(group)
					.callbackData(DataEntry.of(2, group).asString())
			);
		}

		return InlineKeyboardBuilder.pretty(buttons, 5);
	}

	public static ReplyKeyboardMarkup mainKeyboard() {
		ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(new String[] {});

		markup.addRow("Моё расписание", "Расписание преподавателей");
		markup.addRow("Рабочие программы", "График учебного процесса", "Полезные материалы");
		markup.addRow("Настройки");
		
		markup.resizeKeyboard(true);

		return markup;
	}

	@NonNull
	public static ReplyKeyboardMarkup backToMainMenu() {
		return new ReplyKeyboardMarkup("Вернуться в главное меню").resizeKeyboard(true);
	}

	@NonNull
	public static InlineKeyboardMarkup schedule(@NonNull String selectedDate, boolean daily, boolean apply, @Nullable String teacherId) {
		DataEntry data = DataEntry.of(selectedDate, daily, false);
		if (teacherId != null) data.add(teacherId);

		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

		markup.addRow(new InlineKeyboardButton(daily ? "День" : "Неделя").callbackData(data.deepCopy().set(1, !daily).asString()));

		String currentPeriod;
		if (daily) {
			currentPeriod = selectedDate;
		} else {
			String from = CalendarUtils.getMondayOfWeek(selectedDate);
			String to = CalendarUtils.plusDays(from, 7);

			currentPeriod = from + " - " + to;
		}

		markup.addRow(new InlineKeyboardButton("📅 " + currentPeriod).callbackData(data.deepCopy().asString()));

		markup.addRow(
				new InlineKeyboardButton("◀️ " + (daily ? "Пред. день" : "Пред. неделя"))
						.callbackData(data.deepCopy().set(0, daily ? CalendarUtils.minusDays(selectedDate, 1) : CalendarUtils.minusDays(selectedDate, 7)).asString()),
				new InlineKeyboardButton((daily ? "След. день" : "След. неделя") + " ▶️")
						.callbackData(data.deepCopy().set(0, daily ? CalendarUtils.plusDays(selectedDate, 1) : CalendarUtils.plusDays(selectedDate, 7)).asString())
		);

		if (!apply) {
			markup.addRow(new InlineKeyboardButton("Применить изменения ✅").callbackData(data.deepCopy().set(2, true).asString()));
		}

		if (teacherId != null) {
			markup.addRow(new InlineKeyboardButton("Вернуться к выбору преподавателя").callbackData("backToTeachers"));
		}

		return markup;
	}

	@NonNull
	public static InlineKeyboardMarkup teacherSelection(@NonNull List<String> recentTeachers) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

		for (int i = 0; i < recentTeachers.size(); i++) {
			if (i > 8) break; // На всякий случай

			String teacher = recentTeachers.get(i);

			markup.addRow(new InlineKeyboardButton(teacher).callbackData(teacher));
		}

		return markup;
	}
}

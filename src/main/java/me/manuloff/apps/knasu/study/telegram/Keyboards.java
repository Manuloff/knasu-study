package me.manuloff.apps.knasu.study.telegram;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.manuloff.apps.knasu.study.util.DataEntry;
import me.manuloff.apps.knasu.study.util.InlineKeyboardBuilder;

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
					.callbackData(DataEntry.of("showEnrollmentYears", faculty).asString())
			);
		}

		return InlineKeyboardBuilder.pretty(buttons, 3);
	}

	@NonNull
	public static InlineKeyboardMarkup enrollmentYearsKeyboard(@NonNull DataEntry entry, @NonNull List<String> enrollmentYears) {
		List<InlineKeyboardButton> buttons = new LinkedList<>();
		for (String year : enrollmentYears) {
			buttons.add(new InlineKeyboardButton(year)
					.callbackData(entry.deepCopy().set(0, "showGroups").add(year).asString())
			);
		}

		return InlineKeyboardBuilder.pretty(buttons, 3);
	}

	@NonNull
	public static InlineKeyboardMarkup groupsKeyboard(@NonNull DataEntry entry, @NonNull List<String> groups) {
		List<InlineKeyboardButton> buttons = new LinkedList<>();

		for (String group : groups) {
			buttons.add(new InlineKeyboardButton(group)
					.callbackData(entry.deepCopy().set(0, "setGroup").add(group).asString())
			);
		}

		return InlineKeyboardBuilder.pretty(buttons, 5);
	}

	public static ReplyKeyboardMarkup mainKeyboard() {
		ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(new String[] {});

		markup.addRow("");
		markup.addRow("Моё расписание", "Расписание преподавателей");
		markup.addRow("Рабочие программы", "График учебного процесса", "Полезные материалы");
		markup.addRow("Настройки");

		markup.resizeKeyboard(true);

		return markup;
	}
}

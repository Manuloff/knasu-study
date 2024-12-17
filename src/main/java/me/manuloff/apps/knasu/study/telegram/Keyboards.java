package me.manuloff.apps.knasu.study.telegram;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.manuloff.apps.knasu.study.api.response.WorkingStudyPlanResponse;
import me.manuloff.apps.knasu.study.util.CalendarUtils;
import me.manuloff.apps.knasu.study.util.DataEntry;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Manuloff
 * @since 00:07 11.12.2024
 */
@UtilityClass
public class Keyboards {

	@NonNull
	public static InlineKeyboardMarkup faculties(@NonNull List<String> faculties) {
		List<InlineKeyboardButton> buttons = new LinkedList<>();

		for (String faculty : faculties) {
			buttons.add(new InlineKeyboardButton(faculty)
					.callbackData(DataEntry.of(0, faculty).asString())
			);
		}

		return pretty(buttons, 3);
	}

	@NonNull
	public static InlineKeyboardMarkup enrollmentYears(@NonNull DataEntry entry, @NonNull List<String> enrollmentYears) {
		List<InlineKeyboardButton> buttons = new LinkedList<>();
		for (String year : enrollmentYears) {
			buttons.add(new InlineKeyboardButton(year)
					.callbackData(entry.deepCopy().set(0, 1).add(year).asString())
			);
		}

		return pretty(buttons, 3);
	}

	@NonNull
	public static InlineKeyboardMarkup groups(@NonNull List<String> groups) {
		List<InlineKeyboardButton> buttons = new LinkedList<>();

		for (String group : groups) {
			buttons.add(new InlineKeyboardButton(group)
					.callbackData(DataEntry.of(2, group).asString())
			);
		}

		return pretty(buttons, 4);
	}

	public static ReplyKeyboardMarkup mainMenu() {
		ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(new String[] {});

		markup.addRow("👤 Моё расписание", "👨‍🏫 Расписание преподавателей");
		markup.addRow("📚 Рабочие программы", "📅 Учебный график", "📋 Полезные материалы");
		markup.addRow("⚙️ Изменить учебную группу");
		
		markup.resizeKeyboard(true);

		return markup;
	}

	@NonNull
	public static ReplyKeyboardMarkup backToMainMenu() {
		return new ReplyKeyboardMarkup("🏠 Вернуться в главное меню").resizeKeyboard(true);
	}

	@NonNull
	public static InlineKeyboardMarkup openUrl(@NonNull String text, @NonNull String url) {
		return new InlineKeyboardMarkup(new InlineKeyboardButton(text).url(url));
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
			String to = CalendarUtils.plusDays(from, 6);

			currentPeriod = from + " - " + to;
		}

		markup.addRow(new InlineKeyboardButton("📅 " + currentPeriod).callbackData("ignore"));

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
			markup.addRow(new InlineKeyboardButton("Вернуться к выбору преподавателя ↩️").callbackData("backToTeachers"));
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

	//

	@NonNull
	public static InlineKeyboardMarkup periodSelection(@NonNull WorkingStudyPlanResponse response) {
		InlineKeyboardMarkup markup = pretty(response.getPeriods().stream().map(period -> {
			return new InlineKeyboardButton(period.getName()).callbackData(DataEntry.of("period", period.getName()).asString());
		}).collect(Collectors.toList()), 3);

		if (response.getStateExam() != null) {
			markup.addRow(new InlineKeyboardButton("Государственная итоговая аттестация").callbackData("exam"));
		}

		return markup;
	}

	@NonNull
	public static InlineKeyboardMarkup programSelection(@NonNull String period, @NonNull List<WorkingStudyPlanResponse.Program> programs) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

		for (int i = 0; i < programs.size(); i++) {
			WorkingStudyPlanResponse.Program program = programs.get(i);

			// Название некоторых программ настолько длинное, что они просто не влезут в callbackData, поэтому используем индекс
			markup.addRow(new InlineKeyboardButton(program.getName()).callbackData(DataEntry.of("program", i, period).asString()));
		}

		markup.addRow(new InlineKeyboardButton("Вернуться назад").callbackData("backToPeriods"));

		return markup;
	}

	@NonNull
	public static InlineKeyboardMarkup exam(@NonNull String programUrl) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

		markup.addRow(new InlineKeyboardButton("Программа").url(programUrl));
		markup.addRow(new InlineKeyboardButton("Вернуться назад").callbackData("backToPeriods"));

		return markup;
	}

	@NonNull
	public static InlineKeyboardMarkup program(@NonNull WorkingStudyPlanResponse.Program program, @NonNull String period) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

		markup.addRow(new InlineKeyboardButton("Аннотация").url("https://knastu.ru" + program.getAnnotation()));
		markup.addRow(new InlineKeyboardButton("Рабочая программа дисциплины").url("https://knastu.ru" + program.getWorkingProgramDiscipline()));
		if (program.getAssessmentResources() != null) {
			markup.addRow(new InlineKeyboardButton("Фонд оценочных средств").url("https://knastu.ru" + program.getAssessmentResources()));
		}
		markup.addRow(new InlineKeyboardButton("Вернуться назад").callbackData(DataEntry.of("backToPrograms", period).asString()));

		return markup;
	}

	//

	private static InlineKeyboardMarkup pretty(@NonNull List<InlineKeyboardButton> buttons, int rowSize) {
		if (rowSize < 1) throw new RuntimeException();

		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

		List<InlineKeyboardButton> row = new LinkedList<>();

		for (InlineKeyboardButton button : buttons) {
			row.add(button);

			if (row.size() == rowSize) {
				markup.addRow(row.toArray(new InlineKeyboardButton[0]));
				row.clear();
			}
		}

		if (!row.isEmpty()) {
			markup.addRow(row.toArray(new InlineKeyboardButton[0]));
		}

		return markup;
	}
}

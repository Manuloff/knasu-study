package me.manuloff.apps.knasu.study.telegram.handler.impl.teacherschedule;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.TeachersResponse;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.CallbackHandler;
import me.manuloff.apps.knasu.study.telegram.method.ACallback;
import me.manuloff.apps.knasu.study.telegram.method.DMessage;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;
import me.manuloff.apps.knasu.study.util.CalendarUtils;
import me.manuloff.apps.knasu.study.util.DataEntry;

import java.util.List;

/**
 * @author Manuloff
 * @since 23:39 14.12.2024
 */
public class TeacherSelectionCallback extends CallbackHandler {
	public TeacherSelectionCallback() {
		super(null, UserStage.TEACHER_SELECTION);
	}

	@Override
	protected void handleCallback(@NonNull CallbackQuery callback, @NonNull DataEntry dataEntry) {
		String fullName = dataEntry.getString(0);

		TeachersResponse.Teacher teacher = KnasuAPI.getTeachers().getByFullName(fullName);
		if (teacher == null) {
			ACallback.of(callback)
					.text("🚨 К сожалению, выбранный вами преподаватель не найден. Пожалуйста, попробуйте позже.")
					.showAlert(true)
					.execute();
			return;
		}

		UserData data = this.userData(callback);

		List<String> recentTeachers = data.getRecentTeachers();
		recentTeachers.removeIf(s -> s.equalsIgnoreCase(fullName));
		recentTeachers.add(0, fullName);

		if (recentTeachers.size() > 8) {
			recentTeachers = recentTeachers.subList(0, 8);
		}

		data.setRecentTeachers(recentTeachers);
		data.setStage(UserStage.TEACHER_SCHEDULE);

		SMessage.of(callback).text("""
				🗓️ Расписание преподавателя *%s* генерируется.
				
				1️⃣ *Переключение формата отображения*:
				Нажмите на кнопку `День` или `Неделя`, чтобы переключаться между расписанием на один день и расписанием на всю неделю.
				
				2️⃣ *Просмотр текущего периода*:
				Кнопка показывает выбранную дату или диапазон, в зависимости от выбранного формата отображения.
				
				3️⃣ *Навигация по расписанию*:
				Используйте кнопки `Пред. день/неделя` и `Следующий день/неделя`, чтобы переходить к нужной дате.
				
				4️⃣ *Изменение параметров*:
				Если вы внесли изменения, появится кнопка `Применить изменения`. Нажмите на нее, чтобы обновить расписание.
				
				📅 Чтобы перейти к расписанию конкретной даты, отправьте сообщение с нужной датой в формате `дд.мм.гггг`.
				
				Приятного использования! 🚀
				""", teacher.getFullName())
				.execute();

		SMessage.of(callback).text("""
						🌐 Для удобства просмотра расписания вы можете открыть его в браузере. Нажмите на кнопку ниже, чтобы перейти. 🔗
						""")
				.replyMarkup(Keyboards.openUrl("🌐 Открыть в браузере", "https://knastu.ru/teachers/schedule/" + teacher.getId() + "?day=" + CalendarUtils.getCurrentDay()))
				.execute();

		DMessage.of(callback).execute();

		TeacherScheduleCommand.updateSchedule(
				callback.from().id(),
				-1,
				teacher.getId(),
				CalendarUtils.getCurrentDay(),
				true,
				true
		);
	}
}

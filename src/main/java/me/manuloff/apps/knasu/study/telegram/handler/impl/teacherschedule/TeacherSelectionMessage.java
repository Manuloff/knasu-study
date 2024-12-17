package me.manuloff.apps.knasu.study.telegram.handler.impl.teacherschedule;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.TeachersResponse;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.MessageHandler;
import me.manuloff.apps.knasu.study.telegram.handler.impl.StartCommand;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Manuloff
 * @since 22:50 14.12.2024
 */
public class TeacherSelectionMessage extends MessageHandler {

	public TeacherSelectionMessage() {
		super(UserStage.TEACHER_SELECTION);
	}

	@Override
	public void handleMessage(@NonNull Message message) {
		String text = message.text();
		long userId = message.from().id();


		if (text.equalsIgnoreCase("🏠 Вернуться в главное меню")) {
			TeacherScheduleCommand.removeMessageFromSession(userId);
			StartCommand.send(userId);
			return;
		}

		if (text.length() < 5) {
			SMessage.of(message).text("""
					⚠️ Ваш запрос слишком короткий. Пожалуйста, введите минимум 5 символов для поиска.
					""").execute();
			return;
		}

		Set<String> teachers = this.findTeachers(text);
		if (teachers.isEmpty()) {
			SMessage.of(message).text("""
					❌ По вашему запросу *%s* не удалось найти ни одного преподавателя. Попробуйте изменить запрос или уточнить его.
					""", text).execute();
			return;
		}

		SMessage.of(message).text("""
						🔍 По вашему запросу *%s* найдено %s совпадений. Выберите нужного преподавателя из списка.
						""", text, Math.min(teachers.size(), 8))
				.replyMarkup(Keyboards.teacherSelection(teachers.stream().toList())).execute();
	}

	private Set<String> findTeachers(@NonNull String query) {
		TeachersResponse response = KnasuAPI.getTeachers();
		Set<String> teachers = new LinkedHashSet<>();

		// Для чудо-пользователей
		TeachersResponse.Teacher teacher = response.getByFullName(query);
		if (teacher != null) {
			teachers.add(teacher.getFullName());
			return teachers;
		}

		teachers.addAll(response.getByLastName(query).stream().map(TeachersResponse.Teacher::getFullName).toList());
		teachers.addAll(response.getByQuery(query).stream().map(TeachersResponse.Teacher::getFullName).toList());

		return teachers;
	}
}

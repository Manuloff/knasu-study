package me.manuloff.apps.knasu.study.telegram.handler.impl.teacherschedule;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.handler.MessageHandler;
import me.manuloff.apps.knasu.study.telegram.handler.impl.StartCommand;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;
import me.manuloff.apps.knasu.study.util.CalendarUtils;

import java.util.regex.Pattern;

/**
 * @author Manuloff
 * @since 23:41 14.12.2024
 */
public class TeacherScheduleMessage extends MessageHandler {

	private static final Pattern DATE_PATTERN = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}$");

	public TeacherScheduleMessage() {
		super(UserStage.TEACHER_SCHEDULE);
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

		if (DATE_PATTERN.matcher(text).find()) {
			TeacherScheduleCommand.removeMessageFromSession(userId);
			TeacherScheduleCommand.Session session = TeacherScheduleCommand.sessions.get(userId);

			int messageId = SMessage.of(message).text("⏳ Расписание на дату *%s* загружается. Пожалуйста, подождите...", text)
					.execute().message().messageId();

			TeacherScheduleCommand.updateSchedule(userId, messageId, session.getTeacherId(), text, session.isDaily(), true);
			return;
		}

		SMessage.of(message).text("""
				❌ Введенная дата не распознана. Пожалуйста, используйте правильный формат: `дд.мм.гггг` (например, %s).
				""", CalendarUtils.getCurrentDay()).execute();
	}
}

package me.manuloff.apps.knasu.study.telegram.handler.impl.teacherschedule;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.handler.MessageHandler;
import me.manuloff.apps.knasu.study.telegram.handler.impl.StartCommand;
import me.manuloff.apps.knasu.study.telegram.method.DMessage;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;

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
		long id = message.from().id();

		if (text.equalsIgnoreCase("Вернуться в главное меню")) {
			TeacherScheduleCommand.Session session = TeacherScheduleCommand.sessions.get(id);
			int messageId = session.getMessageId();
			if (messageId != -1) {
				DMessage.of(id, messageId).execute();
				session.setMessageId(-1);
			}

			StartCommand.send(id);
			return;
		}

		if (DATE_PATTERN.matcher(text).find()) {
			TeacherScheduleCommand.Session session = TeacherScheduleCommand.sessions.get(id);
			int messageId = session.getMessageId();
			if (messageId != -1) {
				DMessage.of(id, messageId).execute();
				session.setMessageId(-1);
			}

			TeacherScheduleCommand.updateSchedule(id, messageId, session.getTeacherId(), text, session.isDaily(), true);
			return;
		}

		SMessage.of(message).text("Неизвестный формат. Используйте \"дд.мм.гггг\"").execute();
	}
}

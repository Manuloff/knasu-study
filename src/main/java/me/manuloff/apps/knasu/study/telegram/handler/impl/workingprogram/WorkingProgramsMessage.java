package me.manuloff.apps.knasu.study.telegram.handler.impl.workingprogram;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.handler.MessageHandler;
import me.manuloff.apps.knasu.study.telegram.handler.impl.StartCommand;

/**
 * @author Manuloff
 * @since 19:11 15.12.2024
 */
public class WorkingProgramsMessage extends MessageHandler {

	public WorkingProgramsMessage() {
		super(UserStage.WORKING_PROGRAM);
	}

	@Override
	public void handleMessage(@NonNull Message message) {
		String text = message.text();

		if (text.equalsIgnoreCase("🏠 Вернуться в главное меню")) {
			long userId = message.from().id();

			WorkingProgramsCommand.removeMessagesSafety(userId);
			StartCommand.send(userId);
		}
	}
}

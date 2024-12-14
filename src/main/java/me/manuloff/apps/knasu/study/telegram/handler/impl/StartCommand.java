package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.MessageHandler;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;

/**
 * @author Manuloff
 * @since 16:02 14.12.2024
 */
public class StartCommand extends MessageHandler {

	public StartCommand() {
		super("/start");
	}

	@Override
	public void handleMessage(@NonNull Message message) {
		send(message.from().id());
	}

	public static void send(long userId) {
		UserData.of(userId).setStage(UserStage.MAIN_MENU);

		SMessage.of(userId).text("Главное меню:")
				.replyMarkup(Keyboards.mainKeyboard()).execute();
	}
}

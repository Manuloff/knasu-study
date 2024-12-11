package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.MessageHandler;

/**
 * @author Manuloff
 * @since 00:30 11.12.2024
 */
public class StartCmdHandler extends MessageHandler {

	public StartCmdHandler() {
		super("/start", UserStage.MAIN_MENU);
	}

	@Override
	public void handleMessage(@NonNull Message message) {
		this.userData(message).setStage(UserStage.MAIN_MENU);

		this.manager().answer(message, "Главное меню:", Keyboards.mainKeyboard());
	}
}

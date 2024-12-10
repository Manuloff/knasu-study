package me.manuloff.apps.knasu.study.telegram.scenario.impl;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.telegram.TelegramManager;
import me.manuloff.apps.knasu.study.telegram.scenario.AbstractScenario;
import me.manuloff.apps.knasu.study.util.InlineKeyboardBuilder;

/**
 * @author Manuloff
 * @since 23:06 09.12.2024
 */
public final class InitialScenario extends AbstractScenario {


	@Override
	public boolean check(@NonNull Message message) {
		return UserData.of(message.from().id()).getGroup() == null;
	}

	@Override
	public void execute(@NonNull TelegramManager manager, @NonNull Message message) {
		manager.answer(message, "Ну привет, сладкий :3");
		manager.answer(message, "Давай-ка пройдем с тобой регистрацию");

		try {
			InlineKeyboardMarkup markup = new InlineKeyboardMarkup(new InlineKeyboardButton().);
			manager.answer(message, "Используй меню снизу для выбора своей группы или напиши мне её в ответном сообщении",
					markup);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

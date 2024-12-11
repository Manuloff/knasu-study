package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.MessageHandler;

/**
 * @author Manuloff
 * @since 22:15 10.12.2024
 */
public class MessageGroupSelectionHandler extends MessageHandler {
	public MessageGroupSelectionHandler() {
		super(UserStage.GROUP_SELECTION);
	}

	@Override
	public void handleMessage(@NonNull Message message) {
		String text = message.text();

		String fixedGroupName = KnasuAPI.getGroups().getAllGroups().stream().filter(s -> s.equalsIgnoreCase(text)).findFirst().orElse(null);

		if (fixedGroupName == null) {
			this.manager().answer(message, "Странно, но я не вижу группу \"" + text + "\" в списках.\nВозможно, ты допустил ошибку?");
			return;
		}

		this.userData(message).setGroup(fixedGroupName);
		this.userData(message).setStage(UserStage.MAIN_MENU);

		this.manager().answer(message, "Отлично, теперь ты относишься к группе \"" + fixedGroupName + "\"", Keyboards.mainKeyboard());
	}
}

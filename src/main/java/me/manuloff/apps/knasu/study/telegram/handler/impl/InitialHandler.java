package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.AbstractHandler;
import me.manuloff.apps.knasu.study.telegram.handler.HandlerType;

import java.util.List;

/**
 * @author Manuloff
 * @since 19:51 10.12.2024
 */
public final class InitialHandler extends AbstractHandler<Message> {
	public InitialHandler() {
		super(HandlerType.MESSAGE, null);
	}

	@Override
	protected boolean handle(@NonNull Message update) {
		UserData data = UserData.of(update.from().id());
		boolean result = data.getGroup() == null && data.getStage() == null;
		if (result) {
			this.handleStart(update);
		}

		return result;
	}

	private void handleStart(@NonNull Message message) {
		this.manager().answer(message, "Ну привет, сладкий :3");
		this.manager().answer(message, "Давай-ка пройдем с тобой регистрацию");
		this.manager().answer(message, "Для использования бота-помощника, тебе нужно указать свою группу.\nПросто напиши мне свою учебную группу в ответном сообщении");

		this.userData(message).setStage(UserStage.GROUP_SELECTION);

		List<String> faculties = KnasuAPI.getGroups().getFaculties();

		this.manager().answer(message,
				"Либо найди свою учебную группу, используя клавиатуру.\n" +
						"Для начала выбери свою кафедру.",
				Keyboards.facultiesKeyboard(faculties));
	}
}

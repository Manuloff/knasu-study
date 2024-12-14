package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.AbstractHandler;
import me.manuloff.apps.knasu.study.telegram.handler.HandlerType;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;

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
		UserData data = this.userData(update);

		boolean result = data.getGroup() == null && data.getStage() == null;
		if (result) {
			this.handleStart(update);
		}

		return result;
	}

	private void handleStart(@NonNull Message message) {
		SMessage.of(message).text("""
				Привет, %s! 👋
				
				Я твой умный помощник для учебы. Я помогу тебе с расписанием, рабочими программами, календарным графиком и полезными материалами для подготовки к экзаменам.✨
				""", message.from().firstName()).execute();

		SMessage.of(message).text("""
				Чтобы начать, выбери свою учебную группу. Это необходимо, чтобы я мог предоставить тебе персонализированную информацию.
				
				Ты можешь написать название своей группы в ответном сообщении.
				""").execute();

		this.userData(message).setStage(UserStage.GROUP_SELECTION);

		List<String> faculties = KnasuAPI.getGroups().getFaculties();

		SMessage.of(message)
				.text("Также ты можешь использовать кнопки меню, чтобы выбрать свой факультет.")
				.replyMarkup(Keyboards.facultiesKeyboard(faculties))
				.execute();
	}
}

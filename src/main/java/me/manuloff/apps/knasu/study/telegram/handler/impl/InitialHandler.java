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
		this.userData(message).setStage(UserStage.GROUP_SELECTION);

		List<String> faculties = KnasuAPI.getGroups().getFaculties();

		SMessage.of(message).text("""
				👋 Привет, *%s*!
				
				Я - ваш умный цифровой помощник. Я помогу вам быстро и удобно получить всю необходимую информацию о вашем учебном процессе. Вот основные функции, которые я могу предложить:
				- \uD83D\uDC64 Ваше расписание
				- \uD83D\uDC68\u200D\uD83C\uDFEB Расписание преподавателей
				- \uD83D\uDCDA Информацию о рабочих программах
				- \uD83D\uDCC5 Календарный учебный график
				- \uD83D\uDCCB Методические материалы
				
				Давайте начнём! 😊
				""", message.from().firstName()).execute();

		SMessage.of(message).text("""
				📌 Для использования всех моих функций, пожалуйста, укажите вашу учебную группу. Вы можете сделать это, написав название группы в ответном сообщении.
				
				Например: `4ИТб-2`.
				""").execute();

		SMessage.of(message).text("""
				📌 Если вы не знаете точное название группы, вы можете выбрать её с помощью клавиатуры. Для этого сначала выберите свой факультет:
				""")
				.replyMarkup(Keyboards.faculties(faculties))
				.execute();
	}
}

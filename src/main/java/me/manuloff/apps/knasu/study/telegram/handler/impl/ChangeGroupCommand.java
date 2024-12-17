package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.CommandHandler;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;

import java.util.List;

/**
 * @author Manuloff
 * @since 02:03 17.12.2024
 */
public class ChangeGroupCommand extends CommandHandler {
	public ChangeGroupCommand() {
		super("/change_group", "⚙️ Изменить учебную группу");
	}

	@Override
	public void handleCommand(@NonNull Message message) {
		UserData data = this.userData(message);
		data.setStage(UserStage.GROUP_SELECTION);

		long userId = message.from().id();

		List<String> faculties = KnasuAPI.getGroups().getFaculties();

		SMessage.of(userId).text("📝 Введите название учебной группы в ответном сообщении.")
				.replyMarkup(new ReplyKeyboardRemove())
				.execute();

		SMessage.of(userId).text("🎓 Или вы можете выбрать свою учебную группу с помощью меню. Для начала выберите факультет:")
				.replyMarkup(Keyboards.faculties(faculties))
				.execute();
	}
}

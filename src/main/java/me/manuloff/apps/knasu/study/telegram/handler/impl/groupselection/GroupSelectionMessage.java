package me.manuloff.apps.knasu.study.telegram.handler.impl.groupselection;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.MessageHandler;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;

/**
 * @author Manuloff
 * @since 22:15 10.12.2024
 */
public final class GroupSelectionMessage extends MessageHandler {
	public GroupSelectionMessage() {
		super(UserStage.GROUP_SELECTION);
	}

	@Override
	public void handleMessage(@NonNull Message message) {
		String text = message.text();

		String fixedGroupName = KnasuAPI.getGroups().getAllGroups().stream().filter(s -> s.equalsIgnoreCase(text)).findFirst().orElse(null);

		if (fixedGroupName == null) {
			SMessage.of(message).text("""
					🚫 Группа с именем *%s* не найдена. Пожалуйста, проверьте правильность ввода и попробуйте ещё раз.
					""", text).execute();
			return;
		}

		UserData data = this.userData(message);
		boolean firstStart = data.getGroup() == null;

		data.setGroup(fixedGroupName);
		data.setStage(UserStage.MAIN_MENU);

		String msg;

		if (firstStart) {
			this.manager().showBotCommandFor(message.from().id());

			msg = """
							✅ Вы успешно выбрали группу: *%s*.
							
							Теперь вы можете воспользоваться всеми моими возможностями. Приятного использования! 😊
							""";
		} else {
			msg = """
							✅ Вы успешно выбрали группу: *%s*.
							""";
		}

		SMessage.of(message).text(msg, fixedGroupName)
				.replyMarkup(Keyboards.mainMenu()).execute();
	}
}

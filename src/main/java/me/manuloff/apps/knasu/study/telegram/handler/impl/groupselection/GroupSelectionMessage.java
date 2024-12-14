package me.manuloff.apps.knasu.study.telegram.handler.impl.groupselection;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
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
					К сожалению, мы не смогли найти группу с названием «%s». 😕
					
					Пожалуйста, проверь правильность написания или попробуй выбрать группу через меню.
					""", text).execute();
			return;
		}

		this.userData(message).setGroup(fixedGroupName);
		this.userData(message).setStage(UserStage.MAIN_MENU);

		this.manager().showCommandsFor(message.from().id());

		SMessage.of(message).text("""
				Отлично! Мы нашли твою группу: «%s». 🎉
				
				Теперь ты можешь пользоваться всеми функциями бота.
				""", fixedGroupName)
				.replyMarkup(Keyboards.mainKeyboard()).execute();
	}
}

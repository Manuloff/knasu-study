package me.manuloff.apps.knasu.study.telegram.handler.impl.help;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.handler.AbstractHandler;
import me.manuloff.apps.knasu.study.telegram.handler.HandlerType;
import org.jetbrains.annotations.Nullable;

/**
 * @author Manuloff
 * @since 14:44 15.12.2024
 */
public class HelpCommand extends AbstractHandler<Message> {
	public HelpCommand() {
		super(HandlerType.MESSAGE, null);
	}

	@Override
	protected boolean handle(@NonNull Message update) {
		String text = update.text();
		long userId = update.from().id();

		if (text.equalsIgnoreCase("/help")
				|| this.userData(update).getStage() == UserStage.MAIN_MENU && text.equalsIgnoreCase("Помощь")) {

			handle(userId, null);

		}

		return false;
	}


	public static void handle(long userId, @Nullable String selected) {

	}
}

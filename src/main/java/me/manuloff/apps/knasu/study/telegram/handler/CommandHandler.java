package me.manuloff.apps.knasu.study.telegram.handler;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.data.UserStage;
import org.jetbrains.annotations.Nullable;

/**
 * @author Manuloff
 * @since 23:12 15.12.2024
 */
public abstract class CommandHandler extends AbstractHandler<Message> {

	// Используется для обработки команд бота и текста при UserStage == MAIN_MENU

	@NonNull
	private final String command;
	@Nullable
	private final String buttonText;

	public CommandHandler(@NonNull String command, @Nullable String buttonText) {
		super(HandlerType.MESSAGE, null);
		this.command = command;
		this.buttonText = buttonText;
	}

	@Override
	protected final boolean handle(@NonNull Message update) {
		String text = update.text();

		if (text.equalsIgnoreCase(this.command)
				|| (text.equalsIgnoreCase(this.buttonText) && this.userData(update).getStage() == UserStage.MAIN_MENU)) {

			this.handleCommand(update);
			return true;
		}

		return false;
	}

	public abstract void handleCommand(@NonNull Message message);
}

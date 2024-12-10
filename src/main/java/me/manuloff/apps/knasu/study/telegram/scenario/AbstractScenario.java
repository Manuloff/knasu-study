package me.manuloff.apps.knasu.study.telegram.scenario;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.telegram.TelegramManager;

/**
 * @author Manuloff
 * @since 23:04 09.12.2024
 */
public abstract class AbstractScenario {

	public abstract boolean check(@NonNull Message message);

	public abstract void execute(@NonNull TelegramManager manager, @NonNull Message message);
}

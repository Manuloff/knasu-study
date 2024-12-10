package me.manuloff.apps.knasu.study.telegram.scenario.impl;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.manuloff.apps.knasu.study.telegram.TelegramManager;
import me.manuloff.apps.knasu.study.telegram.scenario.AbstractScenario;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Manuloff
 * @since 23:24 09.12.2024
 */
@RequiredArgsConstructor
public final class AwaitMessageScenario extends AbstractScenario {

	private final Map<Long, Function<Message, Boolean>> awaitingMessages;

	@Override
	public boolean check(@NonNull Message message) {
		Long id = message.from().id();

		Function<Message, Boolean> function = this.awaitingMessages.remove(id);
		if (function != null) {
			if (!function.apply(message)) {
				this.awaitingMessages.put(id, function);
			}

			return true;
		}

		return false;
	}

	@Override
	public void execute(@NonNull TelegramManager manager, @NonNull Message message) {

	}
}

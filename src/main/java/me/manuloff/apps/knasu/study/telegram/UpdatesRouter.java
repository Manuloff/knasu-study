package me.manuloff.apps.knasu.study.telegram;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.manuloff.apps.knasu.study.telegram.handler.AbstractHandler;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author Manuloff
 * @since 22:45 09.12.2024
 */
@Log4j2
@RequiredArgsConstructor
public final class UpdatesRouter implements UpdatesListener {

	private final TelegramManager manager;

	@Override
	public int process(List<Update> list) {
		for (Update update : list) {
			this.handle(() -> {
				Message message = update.message();
				if (message != null) {
					// Костыль, который позволяет вызвать TelegramManager.awaitMessage внутри предиката
					Predicate<Message> predicate = this.manager.getAwaitingMessages().remove(message.from().id());
					if (predicate != null) {
						if (!predicate.test(message)) {
							this.manager.getAwaitingMessages().put(message.from().id(), predicate);
						}

						return;
					}
				}

				CallbackQuery callback = update.callbackQuery();
				if (callback != null) {
					Predicate<CallbackQuery> predicate = this.manager.getAwaitingCallbackQueries().remove(callback.from().id());
					if (predicate != null) {
						if (!predicate.test(callback)) {
							this.manager.getAwaitingCallbackQueries().put(callback.from().id(), predicate);
						}

						return;
					}
				}

				for (AbstractHandler<?> handler : this.manager.getHandlers()) {
					if (handler.tryHandle(update)) {
						return;
					}
				}
			});
		}

		return UpdatesListener.CONFIRMED_UPDATES_ALL;
	}

	private void handle(@NonNull Runnable runnable) {
		KnasuStudy.getInstance().getExecutorService().submit(() -> {
			try {
				runnable.run();
			} catch (Exception e) {
				log.error(e);
			}
		});
	}
}

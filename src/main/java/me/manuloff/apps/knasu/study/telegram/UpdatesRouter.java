package me.manuloff.apps.knasu.study.telegram;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.manuloff.apps.knasu.study.telegram.handler.AbstractHandler;

import java.util.List;

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
				e.printStackTrace();
			}
		});
	}
}

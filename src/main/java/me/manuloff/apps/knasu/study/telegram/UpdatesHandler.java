package me.manuloff.apps.knasu.study.telegram;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.manuloff.apps.knasu.study.telegram.scenario.AbstractScenario;

import java.util.List;

/**
 * @author Manuloff
 * @since 22:45 09.12.2024
 */
@Log4j2
@RequiredArgsConstructor
public final class UpdatesHandler implements UpdatesListener {

	private final TelegramManager manager;

	@Override
	public int process(List<Update> list) {
		for (Update update : list) {
			Message message = update.message();
			if (message == null) continue;

			System.out.println(message);

			KnasuStudy.getInstance().getExecutorService().submit(() -> {
				try {
					this.handleMessage(message);
				} catch (Exception e) {
					log.error(e);
				}
			});
		}

		return UpdatesListener.CONFIRMED_UPDATES_ALL;
	}

	private void handleMessage(@NonNull Message message) {
		for (AbstractScenario scenario : this.manager.getScenarios()) {
			if (scenario.check(message)) {
				scenario.execute(this.manager, message);
				break;
			}
		}
	}
}

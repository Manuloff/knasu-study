package me.manuloff.apps.knasu.study.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.manuloff.apps.knasu.study.telegram.scenario.AbstractScenario;
import me.manuloff.apps.knasu.study.telegram.scenario.impl.AwaitMessageScenario;
import me.manuloff.apps.knasu.study.telegram.scenario.impl.InitialScenario;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Manuloff
 * @since 22:44 09.12.2024
 */
public final class TelegramManager {

	@Getter
	private final TelegramBot bot;

	private final Map<Long, Function<Message, Boolean>> awaitingMessages = new HashMap<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<AbstractScenario> scenarios = new LinkedList<>();

	public TelegramManager() {
		this.bot = new TelegramBot(KnasuStudy.getInstance().getAppConfig().getTelegramToken());

		this.bot.setUpdatesListener(new UpdatesHandler(this));

		this.registerScenario(new AwaitMessageScenario(this.awaitingMessages));
		this.registerScenario(new InitialScenario());
	}

	private void registerScenario(@NonNull AbstractScenario scenario) {
		this.scenarios.add(scenario);
	}

	//

	public void awaitMessage(long userId, @NonNull Function<Message, Boolean> function) {
		this.awaitingMessages.put(userId, function);
	}

	public void answer(@NonNull Message message, @NonNull String text) {
		this.answer(message, text, sendMessage -> {});
	}

	public void answer(@NonNull Message message, @NonNull String text, @NonNull Keyboard keyboard) {
		this.answer(message, text, sendMessage -> sendMessage.replyMarkup(keyboard));
	}

	public void answer(@NonNull Message message, @NonNull String text, @NonNull Consumer<SendMessage> consumer) {
		SendMessage request = new SendMessage(message.from().id(), text);
		consumer.accept(request);

		this.bot.execute(request);
	}

}

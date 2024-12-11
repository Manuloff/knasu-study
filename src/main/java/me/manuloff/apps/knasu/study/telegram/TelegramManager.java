package me.manuloff.apps.knasu.study.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.manuloff.apps.knasu.study.telegram.handler.AbstractHandler;
import me.manuloff.apps.knasu.study.telegram.handler.impl.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Manuloff
 * @since 22:44 09.12.2024
 */
@Log4j2
public final class TelegramManager {

	@Getter
	private final TelegramBot bot;

	@Getter(AccessLevel.PACKAGE)
	private final Map<Long, Predicate<Message>> awaitingMessages = new HashMap<>();
	@Getter(AccessLevel.PACKAGE)
	private final Map<Long, Predicate<CallbackQuery>> awaitingCallbackQueries = new HashMap<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<AbstractHandler<?>> handlers = new LinkedList<>();

	public TelegramManager() {
		this.bot = new TelegramBot(KnasuStudy.getInstance().getAppConfig().getTelegramToken());

		this.bot.setUpdatesListener(new UpdatesRouter(this));

		this.registerHandler(new InitialHandler());
		this.registerHandler(new MessageGroupSelectionHandler());
		this.registerHandler(new ShowEnrollmentYearsHandler());
		this.registerHandler(new ShowGroupsHandler());
		this.registerHandler(new SetGroupHandler());
		this.registerHandler(new StartCmdHandler());
	}

	private void registerHandler(@NonNull AbstractHandler<?> handler) {
		this.handlers.add(handler);
	}

	//

	public void awaitMessage(long userId, @NonNull Predicate<Message> predicate) {
		this.awaitingMessages.put(userId, predicate);
	}

	public void awaitCallback(long userId, @NonNull Predicate<CallbackQuery> predicate) {
		this.awaitingCallbackQueries.put(userId, predicate);
	}

	public void answer(@NonNull Message message, @NonNull String text) {
		this.answer(message.from().id(), text, sendMessage -> {});
	}

	public void answer(@NonNull Message message, @NonNull String text, @NonNull Keyboard keyboard) {
		this.answer(message.from().id(), text, sendMessage -> sendMessage.replyMarkup(keyboard));
	}

	public void answer(@NonNull CallbackQuery callback, @NonNull String text) {
		this.answer(callback.from().id(), text, sendMessage -> {});
	}

	public void answer(@NonNull CallbackQuery callback, @NonNull String text, @NonNull Keyboard keyboard) {
		this.answer(callback.from().id(), text, sendMessage -> sendMessage.replyMarkup(keyboard));
	}

	public void answer(long chatId, @NonNull String text, @NonNull Consumer<SendMessage> consumer) {
		SendMessage request = new SendMessage(chatId, text);
		consumer.accept(request);

		this.safeExecute(request);
	}

	//

	public void edit(@NonNull CallbackQuery callback, @NonNull String text) {
		this.edit(callback, text, editMessage -> {});
	}

	public void edit(@NonNull CallbackQuery callback, @NonNull String text, @NonNull InlineKeyboardMarkup keyboard) {
		this.edit(callback, text, editMessage -> editMessage.replyMarkup(keyboard));
	}

	public void edit(@NonNull CallbackQuery callback, @NonNull String text, @NonNull Consumer<EditMessageText> consumer) {
		EditMessageText request = new EditMessageText(callback.maybeInaccessibleMessage().chat().id(), callback.maybeInaccessibleMessage().messageId(), text);
		consumer.accept(request);

		this.safeExecute(request);
	}

	private <T extends BaseRequest<T, R>, R extends BaseResponse> void safeExecute(BaseRequest<T, R> request) {
		R response = this.bot.execute(request);
		if (response.isOk()) return;

		log.error("Telegram Error {}: {}", response.errorCode(), response.description());
	}

}

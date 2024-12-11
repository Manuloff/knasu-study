package me.manuloff.apps.knasu.study.telegram.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * @author Manuloff
 * @since 19:22 10.12.2024
 */
@Getter
@RequiredArgsConstructor
public final class HandlerType<T> {

	public static final HandlerType<Message> MESSAGE = new HandlerType<>(Update::message, Message::from);
	public static final HandlerType<CallbackQuery> CALLBACK = new HandlerType<>(Update::callbackQuery, CallbackQuery::from);

	//

	private final Function<Update, T> function;
	private final Function<T, User> userFunction;
}

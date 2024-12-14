package me.manuloff.apps.knasu.study.telegram.method;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Manuloff
 * @since 15:25 14.12.2024
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DMessage extends MethodExecutor<DeleteMessage, BaseResponse> {

	private final long chatId;
	private final int messageId;

	@NonNull
	public static DMessage of(@NonNull CallbackQuery callback) {
		MaybeInaccessibleMessage message = callback.maybeInaccessibleMessage();
		return new DMessage(message.chat().id(), message.messageId());
	}

	public static DMessage of(long chatId, int messageId) {
		return new DMessage(chatId, messageId);
	}

	@Override
	protected DeleteMessage createRequest() {
		return new DeleteMessage(this.chatId, this.messageId);
	}
}

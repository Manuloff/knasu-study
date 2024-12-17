package me.manuloff.apps.knasu.study.telegram.method;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Manuloff
 * @since 14:07 14.12.2024
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EMessage extends MethodExecutor<EditMessageText, BaseResponse> {

	private final long chatId;
	private final int messageId;

	private String text;
	private ParseMode parseMode = ParseMode.Markdown;
	private MessageEntity[] entities;
	private LinkPreviewOptions linkPreviewOptions;
	private InlineKeyboardMarkup replyMarkup;

	@NonNull
	public static EMessage of(@NonNull CallbackQuery callback) {
		MaybeInaccessibleMessage message = callback.maybeInaccessibleMessage();
		return new EMessage(message.chat().id(), message.messageId());
	}

	@NonNull
	public static EMessage of(long chatId, int messageId) {
		return new EMessage(chatId, messageId);
	}

	//

	@NonNull
	public EMessage text(@NonNull String text) {
		this.text = text;
		return this;
	}

	@NonNull
	public EMessage text(@NonNull String text, @NonNull Object... values) {
		this.text = String.format(text, values);
		return this;
	}

	@NonNull
	public EMessage parseMode(@NonNull ParseMode parseMode) {
		this.parseMode = parseMode;
		return this;
	}

	@NonNull
	public EMessage entities(@NonNull MessageEntity... entities) {
		this.entities = entities;
		return this;
	}

	@NonNull
	public EMessage linkPreviewOptions(@NonNull LinkPreviewOptions linkPreviewOptions) {
		this.linkPreviewOptions = linkPreviewOptions;
		return this;
	}

	@NonNull
	public EMessage replyMarkup(@NonNull InlineKeyboardMarkup replyMarkup) {
		this.replyMarkup = replyMarkup;
		return this;
	}

	@Override
	protected EditMessageText createRequest() {
		assert this.text != null;

		EditMessageText request = new EditMessageText(this.chatId, this.messageId, this.text);

		if (this.parseMode != null) request.parseMode(this.parseMode);
		if (this.entities != null) request.entities(this.entities);
		if (this.linkPreviewOptions != null) request.linkPreviewOptions(this.linkPreviewOptions);
		if (this.replyMarkup != null) request.replyMarkup(this.replyMarkup);

		return request;
	}
}

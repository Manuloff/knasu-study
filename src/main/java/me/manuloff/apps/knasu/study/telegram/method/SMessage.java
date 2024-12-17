package me.manuloff.apps.knasu.study.telegram.method;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyParameters;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Manuloff
 * @since 14:29 14.12.2024
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SMessage extends MethodExecutor<SendMessage, SendResponse> {

	private final long chatId;

	private String text;

	private ParseMode parseMode = ParseMode.Markdown;
	private MessageEntity[] entities;
	private LinkPreviewOptions linkPreviewOptions;
	private String messageEffectId;
	private String businessConnectionId;
	private Integer messageThreadId;
	private Boolean disableNotification;
	private ReplyParameters replyParameters;
	private Integer replyToMessageId;
	private Boolean allowSendingWithoutReply;
	private Keyboard replyMarkup;
	private Boolean protectContent;

	@NonNull
	public static SMessage of(long chatId) {
		return new SMessage(chatId);
	}

	@NonNull
	public static SMessage of(@NonNull Message message) {
		return new SMessage(message.from().id());
	}

	@NonNull
	public static SMessage of(@NonNull CallbackQuery callback) {
		return new SMessage(callback.from().id());
	}

	//

	@NonNull
	public SMessage text(@NonNull String text) {
		this.text = text;
		return this;
	}

	@NonNull
	public SMessage text(@NonNull String text, @NonNull Object... values) {
		this.text = String.format(text, values);
		return this;
	}

	@NonNull
	public SMessage parseMode(@NonNull ParseMode parseMode) {
		this.parseMode = parseMode;
		return this;
	}

	@NonNull
	public SMessage entities(@NonNull MessageEntity... entities) {
		this.entities = entities;
		return this;
	}

	@NonNull
	public SMessage linkPreviewOptions(@NonNull LinkPreviewOptions linkPreviewOptions) {
		this.linkPreviewOptions = linkPreviewOptions;
		return this;
	}

	@NonNull
	public SMessage messageEffectId(@NonNull String messageEffectId) {
		this.messageEffectId = messageEffectId;
		return this;
	}

	@NonNull
	public SMessage businessConnectionId(@NonNull String businessConnectionId) {
		this.businessConnectionId = businessConnectionId;
		return this;
	}

	@NonNull
	public SMessage messageThreadId(int messageThreadId) {
		this.messageThreadId = messageThreadId;
		return this;
	}

	@NonNull
	public SMessage disableNotification(boolean disableNotification) {
		this.disableNotification = disableNotification;
		return this;
	}

	@NonNull
	public SMessage replyParameters(@NonNull ReplyParameters replyParameters) {
		this.replyParameters = replyParameters;
		return this;
	}

	@NonNull
	public SMessage replyToMessageId(int replyToMessageId) {
		this.replyToMessageId = replyToMessageId;
		return this;
	}

	@NonNull
	public SMessage allowSendingWithoutReply(boolean allowSendingWithoutReply) {
		this.allowSendingWithoutReply = allowSendingWithoutReply;
		return this;
	}

	@NonNull
	public SMessage replyMarkup(@NonNull Keyboard replyMarkup) {
		this.replyMarkup = replyMarkup;
		return this;
	}

	@NonNull
	public SMessage protectContent(boolean protectContent) {
		this.protectContent = protectContent;
		return this;
	}

	//

	@Override
	protected SendMessage createRequest() {
		assert this.text != null;

		SendMessage request = new SendMessage(this.chatId, this.text);

		if (this.parseMode != null) request.parseMode(this.parseMode);
		if (this.entities != null) request.entities(this.entities);
		if (this.linkPreviewOptions != null) request.linkPreviewOptions(this.linkPreviewOptions);
		if (this.messageEffectId != null) request.messageEffectId(this.messageEffectId);
		if (this.businessConnectionId != null) request.businessConnectionId(this.businessConnectionId);
		if (this.messageThreadId != null) request.messageThreadId(this.messageThreadId);
		if (this.disableNotification != null) request.disableNotification(this.disableNotification);
		if (this.replyParameters != null) request.replyParameters(this.replyParameters);
		if (this.replyToMessageId != null) request.replyToMessageId(this.replyToMessageId);
		if (this.allowSendingWithoutReply != null) request.allowSendingWithoutReply(this.allowSendingWithoutReply);
		if (this.replyMarkup != null) request.replyMarkup(this.replyMarkup);
		if (this.protectContent != null) request.protectContent(this.protectContent);

		return request;
	}

}

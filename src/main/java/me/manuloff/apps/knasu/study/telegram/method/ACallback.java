package me.manuloff.apps.knasu.study.telegram.method;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * @author Manuloff
 * @since 20:34 15.12.2024
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ACallback extends MethodExecutor<AnswerCallbackQuery, BaseResponse> {

	@NonNull
	private final String callbackId;

	@Nullable
	private String text;
	@Nullable
	private Boolean showAlert;
	@Nullable
	private String url;
	@Nullable
	private Integer cacheTime;

	@NonNull
	public static ACallback of(@NonNull String callbackId) {
		return new ACallback(callbackId);
	}

	@NonNull
	public static ACallback of(@NonNull CallbackQuery callback) {
		return new ACallback(callback.id());
	}

	//

	@NonNull
	public ACallback text(@NonNull String text) {
		this.text = text;
		return this;
	}

	@NonNull
	public ACallback showAlert(@Nullable Boolean showAlert) {
		this.showAlert = showAlert;
		return this;
	}

	@NonNull
	public ACallback url(@NonNull String url) {
		this.url = url;
		return this;
	}

	@NonNull
	public ACallback cacheTime(@Nullable Integer cacheTime) {
		this.cacheTime = cacheTime;
		return this;
	}

	//

	@Override
	protected AnswerCallbackQuery createRequest() {
		AnswerCallbackQuery request = new AnswerCallbackQuery(this.callbackId);

		if (this.text != null) request.text(this.text);
		if (this.showAlert != null) request.showAlert(this.showAlert);
		if (this.url != null) request.url(this.url);
		if (this.cacheTime != null) request.cacheTime(this.cacheTime);

		return request;
	}
}

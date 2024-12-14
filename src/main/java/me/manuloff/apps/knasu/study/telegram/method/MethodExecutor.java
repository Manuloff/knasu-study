package me.manuloff.apps.knasu.study.telegram.method;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.KnasuStudy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

/**
 * @author Manuloff
 * @since 14:18 14.12.2024
 */
public abstract class MethodExecutor<T extends BaseRequest<T, R>, R extends BaseResponse> {

	protected abstract T createRequest();

	public final R execute() {
		R response = KnasuStudy.getInstance().getTelegramManager().getBot().execute(this.createRequest());
		this.check(response);

		return response;
	}

	public final void execute(@NonNull Callback<T, R> callback) {
		KnasuStudy.getInstance().getTelegramManager().getBot().execute(this.createRequest(), callback);
	}

	public final void execute(@NonNull Consumer<R> consumer) {
		KnasuStudy.getInstance().getTelegramManager().getBot().execute(this.createRequest(), new Callback<T, R>() {
			@Override
			public void onResponse(T response, R request) {
				MethodExecutor.this.check(request);
				consumer.accept(request);
			}

			@Override
			public void onFailure(T request, IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	private void check(@NonNull R response) {
		if (response.isOk()) {
			return;
		}

		throw new UncheckedIOException(new IOException(response.errorCode() + ": " + response.description()));
	}

}

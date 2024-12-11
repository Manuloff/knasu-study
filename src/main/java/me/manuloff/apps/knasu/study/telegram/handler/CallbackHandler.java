package me.manuloff.apps.knasu.study.telegram.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.util.DataEntry;
import org.jetbrains.annotations.Nullable;

/**
 * @author Manuloff
 * @since 19:30 10.12.2024
 */
public abstract class CallbackHandler extends AbstractHandler<CallbackQuery> {

	private final String action;

	public CallbackHandler(@NonNull String action, @Nullable UserStage stage) {
		super(HandlerType.CALLBACK, stage);

		this.action = action;
	}

	@Override
	@SneakyThrows
	protected boolean handle(@NonNull CallbackQuery update) {
//		JsonObject object = GsonUtil.gson.fromJson(update.data(), JsonObject.class);
//
//		System.out.println("object = " + object);
//
//		boolean result = object.has("action") && object.get("action").getAsString().equalsIgnoreCase(this.action);
//		if (result) {
//			this.handleCallback(update, object);
//		}

		DataEntry dataEntry = DataEntry.fromString(update.data());
		System.out.println("dataEntry = " + dataEntry);

		boolean result = dataEntry.has(0) && dataEntry.getString(0).equalsIgnoreCase(this.action);
		if (result) {
			this.handleCallback(update, dataEntry);
		}

		return result;
	}

	protected abstract void handleCallback(@NonNull CallbackQuery callback, @NonNull DataEntry dataEntry);
}

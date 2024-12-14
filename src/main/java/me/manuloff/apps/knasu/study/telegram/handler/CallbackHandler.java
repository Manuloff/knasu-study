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

	public CallbackHandler(@Nullable String action, @Nullable UserStage stage) {
		super(HandlerType.CALLBACK, stage);

		this.action = action;
	}

	@Override
	@SneakyThrows
	protected boolean handle(@NonNull CallbackQuery update) {
		DataEntry dataEntry = DataEntry.fromString(update.data());

		boolean result = this.action == null || (dataEntry.has(0) && dataEntry.getString(0).equalsIgnoreCase(this.action));
		if (result) {
			this.handleCallback(update, dataEntry);
		}

		return result;
	}

	protected abstract void handleCallback(@NonNull CallbackQuery callback, @NonNull DataEntry dataEntry);
}

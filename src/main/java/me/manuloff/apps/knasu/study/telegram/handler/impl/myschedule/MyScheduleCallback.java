package me.manuloff.apps.knasu.study.telegram.handler.impl.myschedule;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.handler.CallbackHandler;
import me.manuloff.apps.knasu.study.util.DataEntry;

/**
 * @author Manuloff
 * @since 16:09 14.12.2024
 */
public class MyScheduleCallback extends CallbackHandler {
	public MyScheduleCallback() {
		super(null, UserStage.MY_SCHEDULE);
	}

	@Override
	protected void handleCallback(@NonNull CallbackQuery callback, @NonNull DataEntry dataEntry) {
		MyScheduleCommand.updateSchedule(
				callback.from().id(),
				callback.maybeInaccessibleMessage().messageId(),
				dataEntry.getString(0),
				dataEntry.getBoolean(1),
				dataEntry.getBoolean(2)
		);
	}
}

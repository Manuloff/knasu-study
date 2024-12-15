package me.manuloff.apps.knasu.study.telegram.handler.impl.teacherschedule;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.handler.CallbackHandler;
import me.manuloff.apps.knasu.study.telegram.method.DMessage;
import me.manuloff.apps.knasu.study.util.DataEntry;

/**
 * @author Manuloff
 * @since 23:41 14.12.2024
 */
public class TeacherScheduleCallback extends CallbackHandler {
	public TeacherScheduleCallback() {
		super(null, UserStage.TEACHER_SCHEDULE);
	}

	@Override
	protected void handleCallback(@NonNull CallbackQuery callback, @NonNull DataEntry dataEntry) {
		long userId = callback.from().id();

		if (dataEntry.size() == 1 && dataEntry.getString(0).equalsIgnoreCase("backToTeachers")) {
			DMessage.of(callback).execute();
			TeacherScheduleCommand.teacherSelection(userId);
			return;
		}

		TeacherScheduleCommand.updateSchedule(
				userId,
				callback.maybeInaccessibleMessage().messageId(),
				dataEntry.getString(3),
				dataEntry.getString(0),
				dataEntry.getBoolean(1),
				dataEntry.getBoolean(2));
	}
}

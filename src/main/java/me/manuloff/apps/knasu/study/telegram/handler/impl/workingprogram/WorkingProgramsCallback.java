package me.manuloff.apps.knasu.study.telegram.handler.impl.workingprogram;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.handler.CallbackHandler;
import me.manuloff.apps.knasu.study.util.DataEntry;

/**
 * @author Manuloff
 * @since 15:42 15.12.2024
 */
public class WorkingProgramsCallback extends CallbackHandler {
	public WorkingProgramsCallback() {
		super(null, UserStage.WORKING_PROGRAM);
	}

	@Override
	protected void handleCallback(@NonNull CallbackQuery callback, @NonNull DataEntry entry) {
		String action = entry.getString(0);

		String callbackId = callback.id();

		long userId = callback.from().id();
		int messageId = callback.maybeInaccessibleMessage().messageId();

		switch (action) {
			case "period" -> {
				WorkingProgramsCommand.programSelection(userId, messageId, callbackId, entry.getString(1));
			}
			case "program" -> {
				WorkingProgramsCommand.showProgram(userId, messageId, callbackId, entry.getString(2), entry.getInt(1));
			}
			case "exam" -> {
				WorkingProgramsCommand.showExam(userId, messageId, callbackId);
			}
			case "backToPeriods" -> {
				WorkingProgramsCommand.periodSelection(userId, messageId, callbackId);
			}
			case "backToPrograms" -> {
				WorkingProgramsCommand.Session session = WorkingProgramsCommand.sessions.get(userId);
				if (session != null) {
					session.getMessageIds().remove((Integer) messageId);
					WorkingProgramsCommand.removeMessagesSafety(userId);
				}

				WorkingProgramsCommand.programSelection(userId, messageId, callbackId, entry.getString(1));
			}
		}
	}
}

package me.manuloff.apps.knasu.study.telegram.handler.impl.teacherschedule;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.TeachersResponse;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.handler.CallbackHandler;
import me.manuloff.apps.knasu.study.telegram.method.EMessage;
import me.manuloff.apps.knasu.study.util.CalendarUtils;
import me.manuloff.apps.knasu.study.util.DataEntry;

import java.util.List;

/**
 * @author Manuloff
 * @since 23:39 14.12.2024
 */
public class TeacherSelectionCallback extends CallbackHandler {
	public TeacherSelectionCallback() {
		super(null, UserStage.TEACHER_SELECTION);
	}

	@Override
	protected void handleCallback(@NonNull CallbackQuery callback, @NonNull DataEntry dataEntry) {
		String fullName = dataEntry.getString(0);

		TeachersResponse.Teacher teacher = KnasuAPI.getTeachers().getByFullName(fullName);
		if (teacher == null) {
			EMessage.of(callback).text("Указанный преподаватель не найден, попробуй чуть позже");
			return;
		}

		UserData data = this.userData(callback);

		List<String> recentTeachers = data.getRecentTeachers();
		recentTeachers.removeIf(s -> s.equalsIgnoreCase(fullName));
		recentTeachers.add(0, fullName);

		if (recentTeachers.size() > 8) {
			recentTeachers = recentTeachers.subList(0, 8);
		}

		data.setRecentTeachers(recentTeachers);
		data.setStage(UserStage.TEACHER_SCHEDULE);

		TeacherScheduleCommand.updateSchedule(
				callback.from().id(),
				callback.maybeInaccessibleMessage().messageId(),
				teacher.getId(),
				CalendarUtils.getCurrentDay(),
				true,
				true
		);
	}
}

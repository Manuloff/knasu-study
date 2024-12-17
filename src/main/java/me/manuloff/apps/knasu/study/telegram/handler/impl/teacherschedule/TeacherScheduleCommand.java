package me.manuloff.apps.knasu.study.telegram.handler.impl.teacherschedule;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.EditMessageMedia;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.Data;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.ScheduleResponse;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.renderer.ScheduleTableRenderer;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.CommandHandler;
import me.manuloff.apps.knasu.study.telegram.method.DMessage;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;
import me.manuloff.apps.knasu.study.util.CalendarUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Manuloff
 * @since 21:44 14.12.2024
 */
public class TeacherScheduleCommand extends CommandHandler {

	public static Map<Long, Session> sessions = new HashMap<>();

	public TeacherScheduleCommand() {
		super("/teacher_schedule", "\uD83D\uDC68\u200D\uD83C\uDFEB Расписание преподавателей");
	}

	@Override
	public void handleCommand(@NonNull Message message) {
		teacherSelection(message.from().id());
	}

	public static void teacherSelection(long userId) {
		SMessage.of(userId).text("""
						🔍 Выберите преподавателя, чье расписание хотите просмотреть. Введите фрагмент имени, и я найду соответствующего преподавателя для вас.
						""")
				.replyMarkup(Keyboards.backToMainMenu()).execute();

		sessions.put(userId, new Session());

		UserData data = UserData.of(userId);
		data.setStage(UserStage.TEACHER_SELECTION);

		List<String> recentTeachers = data.getRecentTeachers();

		if (!recentTeachers.isEmpty()) {
			SMessage.of(userId).text("""
							🕒 Так же вы можете выбрать одного из недавних преподавателей, чье расписание уже искали.
							""")
					.replyMarkup(Keyboards.teacherSelection(recentTeachers)).execute();
		}
	}

	public static void updateSchedule(long userId, int messageId, @NonNull String teacherId,
									  @NonNull String selectedDate, boolean daily, boolean apply) {
		Session session = sessions.computeIfAbsent(userId, k -> new Session());
		if (session.lock) {
			return;
		}

		session.lock = true;

		InlineKeyboardMarkup markup = Keyboards.schedule(selectedDate, daily, apply, teacherId);

		if (apply) {
			ScheduleResponse schedule = KnasuAPI.getTeacherSchedule(teacherId, selectedDate);
			byte[] bytes = ScheduleTableRenderer.render(schedule, daily ? CalendarUtils.removeYearFromDate(selectedDate) : null);

			if (messageId == -1) {
				SendPhoto request = new SendPhoto(userId, bytes).replyMarkup(markup);
				messageId = KnasuStudy.getInstance().getTelegramManager().getBot().execute(request).message().messageId();
			} else {
				EditMessageMedia request = new EditMessageMedia(userId, messageId, new InputMediaPhoto(bytes))
						.replyMarkup(markup);

				KnasuStudy.getInstance().getTelegramManager().getBot().execute(request);
			}

		} else {
			EditMessageCaption request = new EditMessageCaption(userId, messageId)
					.replyMarkup(markup);

			KnasuStudy.getInstance().getTelegramManager().getBot().execute(request);
		}

		session.lock = false;
		session.messageId = messageId;
		session.daily = daily;
		session.teacherId = teacherId;
	}

	@Data
	public static class Session {
		private int messageId = -1;
		private boolean daily = true;
		private String teacherId;

		private boolean lock;
	}

	public static void removeMessageFromSession(long userId) {
		Session session = sessions.get(userId);
		if (session != null && session.messageId != -1) {
			DMessage.of(userId, session.messageId).execute();
			session.messageId = -1;
		}
	}
}

package me.manuloff.apps.knasu.study.telegram.handler.impl.myschedule;

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
import me.manuloff.apps.knasu.study.telegram.handler.AbstractHandler;
import me.manuloff.apps.knasu.study.telegram.handler.HandlerType;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;
import me.manuloff.apps.knasu.study.util.CalendarUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Manuloff
 * @since 16:06 14.12.2024
 */
public class MyScheduleCommand extends AbstractHandler<Message> {

	public static final Map<Long, Session> sessions = new HashMap<>();

	public MyScheduleCommand() {
		super(HandlerType.MESSAGE, null);
	}

	@Override
	protected boolean handle(@NonNull Message update) {
		String text = update.text();

		if (text.equalsIgnoreCase("/my_schedule")
			|| (this.userData(update).getStage() == UserStage.MAIN_MENU && text.equalsIgnoreCase("Моё расписание"))) {

			sendInfo(update.from().id());

			return true;
		}

		return false;
	}

	public static void sendInfo(long userId) {
		SMessage.of(userId).text("""
				В этом разделе можно посмотреть своё расписание на день или на неделю.
				Используйте кнопки для переключения вида таблица, а так же дней.
				"""
		).replyMarkup(Keyboards.backToMainMenu()).execute();

		UserData.of(userId).setStage(UserStage.MY_SCHEDULE);

		// Предварительно очищаем сессию
		sessions.remove(userId);

		updateSchedule(userId, -1, CalendarUtils.getMondayOfCurrentWeek(), true, true);
	}

	public static void updateSchedule(long userId, int messageId, @NonNull String selectedDate, boolean daily, boolean apply) {
		Session session = sessions.computeIfAbsent(userId, (k) -> new Session());
		if (session.lock) {
			return;
		}

		session.lock = true;

		InlineKeyboardMarkup markup = Keyboards.schedule(selectedDate, daily, apply, null);

		if (apply || messageId == -1) {
			String group = UserData.of(userId).getGroup();
			assert group != null;

			UUID id = KnasuAPI.getGroups().getGroupIdByGroupName(group);
			assert id != null;

			ScheduleResponse schedule = KnasuAPI.getGroupSchedule(id, selectedDate);
			byte[] bytes = ScheduleTableRenderer.render(schedule, daily ? CalendarUtils.removeYearFromDate(selectedDate) : null);

			if (messageId == -1) {
				SendPhoto request = new SendPhoto(userId, bytes)
						.replyMarkup(markup);
				
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

		session.messageId = messageId;
		session.daily = daily;
		session.lock = false;
	}

	@Data
	public static class Session {
		private int messageId;

		private boolean daily;

		private boolean lock;
	}
}

package me.manuloff.apps.knasu.study.telegram.handler.impl.myschedule;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
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
import java.util.Objects;
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
		UserData data = UserData.of(userId);
		data.setStage(UserStage.MY_SCHEDULE);

		UUID id = KnasuAPI.getGroups().getGroupIdByGroupName(Objects.requireNonNull(data.getGroup()));
		assert id != null;

		// Предварительно очищаем сессию
		sessions.remove(userId);

		SMessage.of(userId).text("""
				🗓️ Добро пожаловать в раздел *Моё расписание*!
				
				Здесь вы можете просматривать и управлять своим расписанием. Используйте кнопки для навигации и изменения формата отображения.
				
				Если вам нужна подробная инструкция, используйте /help.
				
				Приятного использования! 🚀
				"""
		).replyMarkup(Keyboards.backToMainMenu()).execute();

		SMessage.of(userId).text("""
						🌐 Хотите просмотреть расписание в браузере?
						
						Нажмите кнопку ниже, чтобы открыть страницу с вашим расписанием в браузере. Это удобно для более детального просмотра или печати
						""")
				.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("Открыть")
						.url("https://knastu.ru/students/schedule/" + id + "?day=" + CalendarUtils.getCurrentDay()))
				).execute();

		updateSchedule(userId, -1, CalendarUtils.getCurrentDay(), true, true);
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

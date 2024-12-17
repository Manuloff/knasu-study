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
import me.manuloff.apps.knasu.study.telegram.handler.CommandHandler;
import me.manuloff.apps.knasu.study.telegram.method.DMessage;
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
public class MyScheduleCommand extends CommandHandler {

	public static final Map<Long, Session> sessions = new HashMap<>();

	public MyScheduleCommand() {
		super("/my_schedule", "\uD83D\uDC64 Моё расписание");
	}

	@Override
	public void handleCommand(@NonNull Message message) {
		long userId = message.from().id();

		UserData data = UserData.of(userId);
		data.setStage(UserStage.MY_SCHEDULE);

		// Предварительно очищаем сессию
		sessions.remove(userId);

		SMessage.of(userId).text("""
				📅 Ваше расписание генерируется.
				
				1️⃣ *Переключение формата отображения*:
				Нажмите на кнопку `День` или `Неделя`, чтобы переключаться между расписанием на один день и расписанием на всю неделю.
				
				2️⃣ *Просмотр текущего периода*:
				Кнопка показывает выбранную дату или диапазон, в зависимости от выбранного формата отображения.
				
				3️⃣ *Навигация по расписанию*:
				Используйте кнопки `Пред. день/неделя` и `Следующий день/неделя`, чтобы переходить к нужной дате.
				
				4️⃣ *Изменение параметров*:
				Если вы внесли изменения, появится кнопка `Применить изменения`. Нажмите на нее, чтобы обновить расписание.
				
				📅 Чтобы перейти к расписанию конкретной даты, отправьте сообщение с нужной датой в формате `дд.мм.гггг`.
				
				Приятного использования! 🚀
				"""
		).replyMarkup(Keyboards.backToMainMenu()).execute();

		UUID id = KnasuAPI.getGroups().getGroupIdByGroupName(Objects.requireNonNull(data.getGroup()));
		assert id != null;

		SMessage.of(userId).text("""
						🌐 Для удобства просмотра расписания вы можете открыть его в браузере. Нажмите на кнопку ниже, чтобы перейти. 🔗
						""")
				.replyMarkup(Keyboards.openUrl("🌐 Открыть в браузере", "https://knastu.ru/students/schedule/" + id + "?day=" + CalendarUtils.getCurrentDay()))
				.execute();

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
		private int messageId = -1;

		private boolean daily;

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

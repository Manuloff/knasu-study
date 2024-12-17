package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.request.SendMediaGroup;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.AcademicCalendarResponse;
import me.manuloff.apps.knasu.study.api.response.EducationalProgramResponse;
import me.manuloff.apps.knasu.study.telegram.TelegramManager;
import me.manuloff.apps.knasu.study.telegram.handler.CommandHandler;
import me.manuloff.apps.knasu.study.telegram.method.DMessage;
import me.manuloff.apps.knasu.study.telegram.method.EMessage;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;

import java.util.Objects;

/**
 * @author Manuloff
 * @since 21:12 15.12.2024
 */
public class AcademicCalendarCommand extends CommandHandler {
	public AcademicCalendarCommand() {
		super("/academic_calendar", "📅 Учебный график");
	}

	@Override
	public void handleCommand(@NonNull Message message) {
		long userId = message.from().id();

		int messageId = SMessage.of(userId)
				.text("🕒 Информация о вашем календарном учебном графике загружается. Пожалуйста, подождите немного.")
				.execute().message().messageId();

		String group = Objects.requireNonNull(userData(message).getGroup());

		String specialityCode = KnasuAPI.getGroupCodes().getCodeByName(group);
		if (specialityCode == null) {
			EMessage.of(userId, messageId)
					.text("⚠️ К сожалению, не удалось найти код вашей специальности.")
					.execute();
			return;
		}

		EducationalProgramResponse program = KnasuAPI.getEducationalProgram(specialityCode);
		String academicCalendarUrl = program.getAcademicCalendarUrl(group);
		if (academicCalendarUrl == null) {
			EMessage.of(userId, messageId)
					.text("⚠️ К сожалению, не удалось получить ваш календарный учебный график.")
					.execute();
			return;
		}

		AcademicCalendarResponse response = KnasuAPI.getAcademicCalendar(academicCalendarUrl);

		DMessage.of(userId, messageId).execute();

		String text;

		if (!response.getImages().isEmpty()) {
			InputMediaPhoto[] media = response.getImages().stream().map(InputMediaPhoto::new).toArray(InputMediaPhoto[]::new);
			TelegramManager.bot().execute(new SendMediaGroup(userId, media));

			text = """
					✅ Календарный учебный график успешно обработан и отправлен. Проверьте фотографии выше.
					
					🌐 Если хотите просмотреть график в формате PDF, нажмите кнопку ниже:
					""";

		} else {
			text = """
					⚠️ К сожалению, я не смог найти календарный учебный график в файле. Возможно, вы сможете найти его самостоятельно.
					
					🌐 Файл с графиком доступен по кнопке ниже:
					""";
		}

		SMessage.of(userId).text(text)
				.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("Открыть PDF")
						.url("https://knastu.ru" + academicCalendarUrl)
				)).execute();
	}
}

package me.manuloff.apps.knasu.study.telegram.handler.impl.workingprogram;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.request.DeleteMessages;
import com.pengrad.telegrambot.request.SendMediaGroup;
import com.pengrad.telegrambot.response.MessagesResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.EducationalProgramResponse;
import me.manuloff.apps.knasu.study.api.response.GroupProgramAnnotation;
import me.manuloff.apps.knasu.study.api.response.WorkingStudyPlanResponse;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.TelegramManager;
import me.manuloff.apps.knasu.study.telegram.handler.CommandHandler;
import me.manuloff.apps.knasu.study.telegram.method.ACallback;
import me.manuloff.apps.knasu.study.telegram.method.DMessage;
import me.manuloff.apps.knasu.study.telegram.method.EMessage;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Manuloff
 * @since 15:38 15.12.2024
 */
@Log4j2
public class WorkingProgramsCommand extends CommandHandler {

	public static final Map<Long, Session> sessions = new HashMap<>();

	public WorkingProgramsCommand() {
		super("/working_programs", "📚 Рабочие программы");
	}

	@Override
	public void handleCommand(@NonNull Message message) {
		long userId = message.from().id();

		userData(message).setStage(UserStage.WORKING_PROGRAM);

		int messageId = SMessage.of(message).text("""
						⏳ Информация о вашей учебной группе загружается. Пожалуйста, подождите немного.
						""")
				.replyMarkup(Keyboards.backToMainMenu())
				.execute().message().messageId();

		String group = Objects.requireNonNull(userData(message).getGroup());

		String specialityCode = KnasuAPI.getGroupCodes().getCodeByName(group);
		if (specialityCode == null) {
			DMessage.of(userId, messageId).execute();
			SMessage.of(userId).text("❌ Не удалось найти код вашей специальности. Возможно, данные еще не загружены или произошла ошибка.").execute();
			return;
		}

		EducationalProgramResponse program = KnasuAPI.getEducationalProgram(specialityCode);
		String workingStudyPlanUrl = program.getWorkingStudyPlanUrl(group);
		if (workingStudyPlanUrl == null) {
			DMessage.of(userId, messageId).execute();
			SMessage.of(userId).text("❌ Не удалось найти ссылку на ваш рабочий план. Возможно, данные еще не загружены или произошла ошибка.").execute();
			return;
		}

		sessions.remove(userId);

		periodSelection(userId, -1, null);
	}


	public static void periodSelection(long userId, int messageId, @Nullable String callbackId) {
		handle(() -> {
			WorkingStudyPlanResponse response = getWorkingStudyPlan(userId);

			InlineKeyboardMarkup markup = Keyboards.periodSelection(response);

			String text = """
					✅ Информация о рабочих программах успешно загружена.
					
					📅 Выберите, за какой период хотите просмотреть дисциплины, или ознакомьтесь с информацией о государственной итоговой аттестации.
					""";

			if (messageId != -1) {
				EMessage.of(userId, messageId).text(text).replyMarkup(markup).execute();

			} else {
				int newMessageId = SMessage.of(userId).text(text).replyMarkup(markup).execute().message().messageId();
				sessions.computeIfAbsent(userId, k -> new Session()).messageIds.add(newMessageId);
			}

		}, callbackId, userId);
	}

	public static void programSelection(long userId, int messageId, @NonNull String callbackId, @NonNull String periodName) {
		handle(() -> {
			WorkingStudyPlanResponse response = getWorkingStudyPlan(userId);

			WorkingStudyPlanResponse.Period period = response.getPeriods().stream().filter(p -> p.getName().equalsIgnoreCase(periodName))
					.findFirst().orElseThrow();

			InlineKeyboardMarkup markup = Keyboards.programSelection(periodName, period.getPrograms());

			EMessage.of(userId, messageId).text("""
							🎓 Список дисциплин, доступных в период *%s*, представлен в меню ниже.
							Выберите интересующую вас дисциплину, чтобы получить подробную информацию о ней.
							""", periodName)
					.replyMarkup(markup).execute();

			sessions.computeIfAbsent(userId, k -> new Session()).messageIds.add(messageId);

		}, callbackId, userId);
	}

	public static void showExam(long userId, int messageId, @NonNull String callbackId) {
		handle(() -> {
			WorkingStudyPlanResponse response = getWorkingStudyPlan(userId);

			InlineKeyboardMarkup markup = Keyboards.exam("https://knastu.ru" + response.getStateExam());

			EMessage.of(userId, messageId).text("""
							📚 Для государственной итоговой аттестации доступна программа, ознакомиться с которой вы можете по кнопке снизу.
							""")
					.replyMarkup(markup).execute();

		}, callbackId, userId);
	}

	public static void showProgram(long userId, int messageId, @NonNull String callbackId, @NonNull String periodName, int programIndex) {
		handle(() -> {
			WorkingStudyPlanResponse response = getWorkingStudyPlan(userId);

			WorkingStudyPlanResponse.Period period = response.getPeriods().stream().filter(p -> p.getName().equalsIgnoreCase(periodName))
					.findFirst().orElseThrow();
			WorkingStudyPlanResponse.Program program = period.getPrograms().get(programIndex);

			GroupProgramAnnotation annotation = KnasuAPI.getGroupProgramAnnotation(program.getAnnotation());

			DMessage.of(userId, messageId).execute();

			InputMediaPhoto[] array = annotation.getImages().stream().map(InputMediaPhoto::new).toArray(InputMediaPhoto[]::new);
			MessagesResponse msgResponse = TelegramManager.bot().execute(new SendMediaGroup(userId, array));

			Session session = sessions.computeIfAbsent(userId, k -> new Session());

			StringBuilder builder = new StringBuilder("""
					📚 Вот что мне удалось найти для дисциплины *%s* за *%s*.
					""");

			if (msgResponse.isOk()) {
				session.messageIds.addAll(Arrays.stream(msgResponse.messages()).map(Message::messageId).toList());

				builder.append("""
						
						🔼 Для быстрого ознакомления с аннотацией, вы можете просмотреть вырезки выше.
						""");
			}

			builder.append("""
					
					📄 Перечень доступных документов:
					 - Аннотация
					 - Рабочая программа дисциплины (РПД)
					""");

			if (program.getAssessmentResources() != null) {
				builder.append("""
						 - Фонд оценочных средств (ФОС)
						""");
			}

			builder.append("""
					
					🔽 Ознакомиться с каждым документом или вернуться к выбору дисциплины можно с помощью кнопок ниже.
					""");

			int newMessageId = SMessage.of(userId).text(builder.toString(), program.getName(), periodName)
					.replyMarkup(Keyboards.program(program, periodName))
					.execute().message().messageId();

			session.messageIds.add(newMessageId);

		}, callbackId, userId);
	}

	@NonNull
	private static WorkingStudyPlanResponse getWorkingStudyPlan(long userId) {
		String group = UserData.of(userId).getGroup();
		assert group != null;

		String code = KnasuAPI.getGroupCodes().getCodeByName(group);
		assert code != null;

		String workingStudyPlanUrl = KnasuAPI.getEducationalProgram(code).getWorkingStudyPlanUrl(group);
		assert workingStudyPlanUrl != null;

		return KnasuAPI.getWorkingProgram(workingStudyPlanUrl);
	}

	private static void handle(@NonNull Runnable runnable, @Nullable String callbackId, long userId) {
		Session session = sessions.computeIfAbsent(userId, k -> new Session());

		if (session.lock) {
			return;
		}

		session.lock = true;

		try {
			runnable.run();

		} catch (Exception e) {
			log.error("Failed to handle", e);

			if (callbackId != null) {
				ACallback.of(callbackId).text("🚨 К сожалению, произошла ошибка при обработке запроса. Пожалуйста, попробуй позже.")
						.showAlert(true)
						.execute();
			}

		} finally {
			session.lock = false;
		}
	}


	@Getter
	public static class Session {
		private final List<Integer> messageIds = new ArrayList<>();

		private boolean lock = false;
	}

	public static void removeMessagesSafety(long userId) {
		try {
			Session session = sessions.get(userId);
			if (session == null) return;

			TelegramManager.bot().execute(new DeleteMessages(userId,
					session.messageIds.stream().mapToInt(Integer::intValue).toArray())
			);

			session.messageIds.clear();

		} catch (Exception e) {}
	}
}

package me.manuloff.apps.knasu.study.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandsScopeChat;
import com.pengrad.telegrambot.request.GetMyCommands;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.GetMyCommandsResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.manuloff.apps.knasu.study.telegram.handler.AbstractHandler;
import me.manuloff.apps.knasu.study.telegram.handler.impl.*;
import me.manuloff.apps.knasu.study.telegram.handler.impl.groupselection.GroupSelectionCallback;
import me.manuloff.apps.knasu.study.telegram.handler.impl.groupselection.GroupSelectionMessage;
import me.manuloff.apps.knasu.study.telegram.handler.impl.myschedule.MyScheduleCallback;
import me.manuloff.apps.knasu.study.telegram.handler.impl.myschedule.MyScheduleCommand;
import me.manuloff.apps.knasu.study.telegram.handler.impl.myschedule.MyScheduleMessage;
import me.manuloff.apps.knasu.study.telegram.handler.impl.teacherschedule.*;
import me.manuloff.apps.knasu.study.telegram.handler.impl.workingprogram.WorkingProgramsCallback;
import me.manuloff.apps.knasu.study.telegram.handler.impl.workingprogram.WorkingProgramsCommand;
import me.manuloff.apps.knasu.study.telegram.handler.impl.workingprogram.WorkingProgramsMessage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Manuloff
 * @since 22:44 09.12.2024
 */
@Log4j2
public final class TelegramManager {

	@Getter
	private final TelegramBot bot;

	@Getter(AccessLevel.PACKAGE)
	private final List<AbstractHandler<?>> handlers = new LinkedList<>();

	public TelegramManager() {
		this.bot = new TelegramBot(KnasuStudy.getInstance().getAppConfig().getTelegramToken());

		this.bot.setUpdatesListener(new UpdatesRouter(this));

		this.registerHandlers();

		this.checkBotCommands();
	}

	private void checkBotCommands() {
		KnasuStudy.getInstance().getExecutorService().submit(() -> {
			log.info("Проверка на актуальность списка команд у пользователей");

			BotCommand[] botCommands = this.getBotCommands();

			for (Long userId : KnasuStudy.getInstance().getUserDatabase().getUserIdsWithGroupNotNull()) {
				GetMyCommandsResponse response = this.bot.execute(new GetMyCommands().scope(new BotCommandsScopeChat(userId)));
				if (!response.isOk()) continue;

				BotCommand[] userCommands = response.commands();

				if (Arrays.equals(userCommands, botCommands)) continue;

				this.showBotCommandFor(userId);
			}

			log.info("Проверка окончена");
		});
	}

	private void registerHandlers() {
		// Инициализация
		this.registerHandler(new InitialHandler());

		// Выбор группы
		this.registerHandler(new GroupSelectionMessage());
		this.registerHandler(new GroupSelectionCallback());

		// Обработка команд и сообщений с меню
		this.registerHandler(new StartCommand());
		this.registerHandler(new MyScheduleCommand());
		this.registerHandler(new TeacherScheduleCommand());
		this.registerHandler(new WorkingProgramsCommand());
		this.registerHandler(new AcademicCalendarCommand());
		this.registerHandler(new MaterialsCommand());
		this.registerHandler(new ChangeGroupCommand());

		// Моё расписание
		this.registerHandler(new MyScheduleMessage());
		this.registerHandler(new MyScheduleCallback());

		// Расписание преподавателя
		this.registerHandler(new TeacherScheduleMessage());
		this.registerHandler(new TeacherScheduleCallback());
		this.registerHandler(new TeacherSelectionMessage());
		this.registerHandler(new TeacherSelectionCallback());

		// Рабочие программы
		this.registerHandler(new WorkingProgramsCallback());
		this.registerHandler(new WorkingProgramsMessage());
	}

	private void registerHandler(@NonNull AbstractHandler<?> handler) {
		this.handlers.add(handler);
	}

	private BotCommand[] getBotCommands() {
		return new BotCommand[] {
				new BotCommand("/start", "Главное меню"),
				new BotCommand("/my_schedule", "Ваше расписание"),
				new BotCommand("/teacher_schedule", "Расписание преподавателей"),
				new BotCommand("/working_programs", "Информация о рабочих программах"),
				new BotCommand("/academic_calendar", "Ваш календарный учебный график"),
				new BotCommand("/materials", "Полезные материалы для вас"),
				new BotCommand("/change_group", "Изменить учебную группу")
		};
	}

	public void showBotCommandFor(long chatId) {
		SetMyCommands commands = new SetMyCommands(this.getBotCommands());
		commands.scope(new BotCommandsScopeChat(chatId));

		this.bot.execute(commands);
	}

	@NonNull
	public static TelegramBot bot() {
		return KnasuStudy.getInstance().getTelegramManager().getBot();
	}
}

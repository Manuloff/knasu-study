package me.manuloff.apps.knasu.study.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandsScopeChat;
import com.pengrad.telegrambot.request.SetMyCommands;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.manuloff.apps.knasu.study.telegram.handler.AbstractHandler;
import me.manuloff.apps.knasu.study.telegram.handler.impl.InitialHandler;
import me.manuloff.apps.knasu.study.telegram.handler.impl.StartCommand;
import me.manuloff.apps.knasu.study.telegram.handler.impl.groupselection.GroupSelectionCallback;
import me.manuloff.apps.knasu.study.telegram.handler.impl.groupselection.GroupSelectionMessage;
import me.manuloff.apps.knasu.study.telegram.handler.impl.myschedule.MyScheduleCallback;
import me.manuloff.apps.knasu.study.telegram.handler.impl.myschedule.MyScheduleCommand;
import me.manuloff.apps.knasu.study.telegram.handler.impl.myschedule.MyScheduleMessage;
import me.manuloff.apps.knasu.study.telegram.handler.impl.teacherschedule.*;

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
	}

	private void registerHandlers() {
		// Инициализация
		this.registerHandler(new InitialHandler());

		// Выбор группы
		this.registerHandler(new GroupSelectionMessage());
		this.registerHandler(new GroupSelectionCallback());

		// Обработка команд
		this.registerHandler(new StartCommand());
		this.registerHandler(new MyScheduleCommand());
		this.registerHandler(new TeacherScheduleCommand());

		// Моё расписание
		this.registerHandler(new MyScheduleMessage());
		this.registerHandler(new MyScheduleCallback());

		// Расписание преподавателя
		this.registerHandler(new TeacherScheduleMessage());
		this.registerHandler(new TeacherScheduleCallback());
		this.registerHandler(new TeacherSelectionMessage());
		this.registerHandler(new TeacherSelectionCallback());
	}

	private void registerHandler(@NonNull AbstractHandler<?> handler) {
		this.handlers.add(handler);
	}

	public void showCommandsFor(long chatId) {
		SetMyCommands commands = new SetMyCommands(
				new BotCommand("/start", "Вызывает начальное меню бота"),
				new BotCommand("/my_schedule", "Показывает ваше расписание"),
				new BotCommand("/teacher_schedule", "Показывает расписание преподавателей"),
				new BotCommand("/working_program", "Показывает информацию о ваших рабочих программах"),
				new BotCommand("/academic_calendar", "Показывает Ваш календарный учебный график"),
				new BotCommand("/materials", "Показывает методические материалы для Вас"),
				new BotCommand("/help", "Помощь в использовании бота")
		);
		commands.scope(new BotCommandsScopeChat(chatId));

		this.bot.execute(commands);
	}
}

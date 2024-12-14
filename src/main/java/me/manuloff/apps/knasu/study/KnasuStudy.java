package me.manuloff.apps.knasu.study;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.manuloff.apps.knasu.study.config.AppConfig;
import me.manuloff.apps.knasu.study.data.UserDataStorage;
import me.manuloff.apps.knasu.study.data.UserDatabase;
import me.manuloff.apps.knasu.study.telegram.TelegramManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Manuloff
 * @since 19:34 09.12.2024
 */
@Log4j2
@Getter
public final class KnasuStudy {

	@Getter
	private static KnasuStudy instance;

	private final ScheduledExecutorService executorService;

	private final AppConfig appConfig;

	private final UserDatabase userDatabase;
	private final UserDataStorage userDataStorage;

	private final TelegramManager telegramManager;

	KnasuStudy() throws IOException, SQLException {
		if (instance != null) {
			throw new RuntimeException();
		}

		instance = this;

		this.executorService = Executors.newScheduledThreadPool(10);

		this.appConfig = AppConfig.load();

		DataSource source = this.appConfig.getDatabaseConfig().getSource();
		this.userDatabase = new UserDatabase(source);
		this.userDataStorage = new UserDataStorage();

		this.telegramManager = new TelegramManager();
	}

}

package me.manuloff.apps.knasu.study.config;

import lombok.Getter;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.Main;
import saharnooby.lib.config.rewrite.ConfigSection;
import saharnooby.lib.config.rewrite.io.ConfigIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Manuloff
 * @since 19:43 09.12.2024
 */
@Getter
public final class AppConfig {

	private final String telegramToken;
	private final DatabaseConfig databaseConfig;

	private final Map<String, String> groupCodes = new HashMap<>();

	public AppConfig(@NonNull ConfigSection section) {
		this.telegramToken = section.strings().require("telegramToken");
		this.databaseConfig = new DatabaseConfig(section.sections().require("database"));

		section.sections().require("groupCodes").strings().forEachChecked(this.groupCodes::put);
	}

	public static AppConfig load() throws IOException {
		File file = new File("config.yml");
		if (Files.notExists(file.toPath())) {
			Files.copy(Objects.requireNonNull(Main.class.getResourceAsStream("/config.yml")), file.toPath());
		}

		return new AppConfig(ConfigIO.YAML.loadSection(file));
	}
}

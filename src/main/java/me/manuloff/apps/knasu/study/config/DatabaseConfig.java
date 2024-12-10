package me.manuloff.apps.knasu.study.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.NonNull;
import saharnooby.lib.config.rewrite.ConfigSection;

import javax.sql.DataSource;

/**
 * @author Manuloff
 * @since 19:45 09.12.2024
 */
public final class DatabaseConfig {

	private final String host;
	private final String username;
	private final String password;
	private final String database;

	@Getter(lazy = true)
	private final DataSource source = create();

	DatabaseConfig(@NonNull ConfigSection section) {
		this.host = section.strings().require("host");
		this.username = section.strings().require("username");
		this.password = section.strings().require("password");
		this.database = section.strings().require("database");
	}

	@NonNull
	private DataSource create() {
		HikariConfig config = new HikariConfig();

		config.setJdbcUrl("jdbc:mysql://" + this.host + "/" + this.database +
				"?characterEncoding=utf-8" +
				"&useUnicode=true" +
				"&useJDBCCompliantTimezoneShift=true" +
				"&useLegacyDatetimeCode=false" +
				"&serverTimezone=UTC");

		config.setUsername(this.username);
		config.setPassword(this.password);

		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		return new HikariDataSource(config);
	}
}

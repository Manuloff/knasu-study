package me.manuloff.apps.knasu.study.data;

import lombok.NonNull;
import lombok.SneakyThrows;
import me.saharnooby.lib.query.orm.DataClass;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * @author Manuloff
 * @since 19:50 09.12.2024
 */
public final class UserDatabase {

	private static final DataClass<UserData> DAO = new DataClass<>(UserData.class);

	private final DataSource source;

	public UserDatabase(@NonNull DataSource source) throws SQLException {
		this.source = source;

		DAO.createTableIfNotExists().update(this.source);
	}

	@NonNull
	@SneakyThrows
	public List<Long> getUserIdsWithGroupNotNull() {
		return DAO.select().col("user_id").whereExpr("`group` IS NOT NULL").queryAndMapAll(this.source, set -> {
			return set.getLong(1);
		});
	}

	@NonNull
	public UserData loadOrCreate(long userId) throws SQLException {
		Optional<UserData> opt = DAO.selectBy(userId).queryAndMap(this.source, DAO);

		if (opt.isEmpty()) {
			UserData data = new UserData(userId);
			DAO.insert(data).update(this.source);

			return data;
		}

		return opt.get();
	}

	public void update(@NonNull UserData data, @NonNull String... fieldNames) throws SQLException {
		DAO.update(data, fieldNames).update(this.source);
	}
}

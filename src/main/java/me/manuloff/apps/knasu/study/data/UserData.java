package me.manuloff.apps.knasu.study.data;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.saharnooby.lib.query.orm.annotation.Column;
import me.saharnooby.lib.query.orm.annotation.Ignore;
import me.saharnooby.lib.query.orm.annotation.Table;
import org.jetbrains.annotations.Nullable;

/**
 * @author Manuloff
 * @since 19:50 09.12.2024
 */
@Table("users")
@RequiredArgsConstructor
public final class UserData {

	@Getter
	@Column(primaryKey = true, notNull = true)
	private final long userId;

	private String group = null;

	@Ignore
	@Getter
	private long lastAccessTime = System.currentTimeMillis();

	public UserData(long userId, @Nullable String group) {
		this.userId = userId;
		this.group = group;
	}

	@Nullable
	public String getGroup() {
		this.lastAccessTime = System.currentTimeMillis();
		return this.group;
	}

	public void setGroup(String group) {
		this.lastAccessTime = System.currentTimeMillis();
		this.group = group;

		this.update("group");
	}

	//

	@SneakyThrows
	private void update(@NonNull String... fieldNames) {
		KnasuStudy.getInstance().getUserDatabase().update(this, fieldNames);
	}

	//

	@SneakyThrows
	public static UserData of(long userId) {
		if (KnasuStudy.getInstance().getUserDataStorage().has(userId)) {
			return KnasuStudy.getInstance().getUserDataStorage().get(userId);

		} else {
			UserData data = KnasuStudy.getInstance().getUserDatabase().loadOrCreate(userId);
			KnasuStudy.getInstance().getUserDataStorage().set(data);

			return data;
		}
	}
}

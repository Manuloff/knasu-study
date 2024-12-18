package me.manuloff.apps.knasu.study.data;

import lombok.*;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.saharnooby.lib.query.orm.annotation.Column;
import me.saharnooby.lib.query.orm.annotation.Ignore;
import me.saharnooby.lib.query.orm.annotation.Table;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Manuloff
 * @since 19:50 09.12.2024
 */
@Table("users")
@ToString
@RequiredArgsConstructor
public final class UserData {

	@Getter
	@Column(primaryKey = true, notNull = true)
	private final long userId;

	private String group;

	private UserStage stage;

	private String recentTeachers;

	@Ignore
	@Getter
	private long lastAccessTime = System.currentTimeMillis();

	public UserData(long userId, @Nullable String group, @Nullable UserStage stage, @Nullable String recentTeachers) {
		this.userId = userId;
		this.group = group;
		this.stage = stage;
		this.recentTeachers = recentTeachers;
	}

	@Nullable
	public String getGroup() {
		this.lastAccessTime = System.currentTimeMillis();
		return this.group;
	}

	public void setGroup(String group) {
		this.lastAccessTime = System.currentTimeMillis();

		if (this.group != null && this.group.equals(group)) return;
		this.group = group;

		this.update("group");
	}

	@Nullable
	public UserStage getStage() {
		this.lastAccessTime = System.currentTimeMillis();

		return this.stage;
	}

	public void setStage(@NonNull UserStage stage) {
		this.lastAccessTime = System.currentTimeMillis();

		if (stage.equals(this.stage)) return;
		this.stage = stage;

		this.update("stage");
	}

	@NonNull
	public List<String> getRecentTeachers() {
		this.lastAccessTime = System.currentTimeMillis();

		return this.recentTeachers != null ? Arrays.stream(this.recentTeachers.split(",")).collect(Collectors.toCollection(LinkedList::new))
				: new LinkedList<>();
	}

	public void setRecentTeachers(@NonNull List<String> teachers) {
		this.lastAccessTime = System.currentTimeMillis();

		this.recentTeachers = String.join(",", teachers);
		this.update("recent_teachers");
	}

	//

	@SneakyThrows
	private void update(@NonNull String... fieldNames) {
		KnasuStudy.getInstance().getUserDatabase().update(this, fieldNames);
	}

	//

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UserData)) return false;
		UserData data = (UserData) o;
		return userId == data.userId;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(userId);
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

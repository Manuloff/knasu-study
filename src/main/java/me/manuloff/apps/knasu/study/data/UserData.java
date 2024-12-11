package me.manuloff.apps.knasu.study.data;

import lombok.*;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.saharnooby.lib.query.orm.annotation.Column;
import me.saharnooby.lib.query.orm.annotation.Ignore;
import me.saharnooby.lib.query.orm.annotation.Table;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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

	private String group = null;

	private UserStage stage = null;

	@Ignore
	@Getter
	private long lastAccessTime = System.currentTimeMillis();

	public UserData(long userId, @Nullable String group, @Nullable UserStage stage) {
		this.userId = userId;
		this.group = group;
		this.stage = stage;
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

//	@NonNull
//	public JsonObject getState() {
//		this.lastAccessTime = System.currentTimeMillis();
//
//		if (this.state != null) {
//			return GsonUtil.gson.fromJson(this.state, JsonObject.class);
//		} else {
//			return new JsonObject();
//		}
//	}
//
//	public void setState(@Nullable JsonObject state) {
//		this.lastAccessTime = System.currentTimeMillis();
//
//		if (state != null) {
//			this.state = GsonUtil.toJson(state);
//		} else {
//			this.state = null;
//		}
//	}

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

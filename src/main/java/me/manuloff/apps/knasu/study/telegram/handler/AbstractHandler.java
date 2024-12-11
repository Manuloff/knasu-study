package me.manuloff.apps.knasu.study.telegram.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.manuloff.apps.knasu.study.KnasuStudy;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.TelegramManager;
import org.jetbrains.annotations.Nullable;

/**
 * @author Manuloff
 * @since 19:21 10.12.2024
 */
@Getter
@RequiredArgsConstructor
public abstract class AbstractHandler<T> {

	@NonNull
	protected final HandlerType<T> type;
	@Nullable
	protected final UserStage stage;

	public final boolean tryHandle(@NonNull Update update) {
		T apply = this.type.getFunction().apply(update);
		if (apply == null) return false;

		if (this.stage != null && !this.stage.equals(UserData.of(this.type.getUserFunction().apply(apply).id()).getStage())) {
			return false;
		}

		return this.handle(apply);
	}

	protected abstract boolean handle(@NonNull T update);

	//

	@NonNull
	protected final TelegramManager manager() {
		return KnasuStudy.getInstance().getTelegramManager();
	}

	@NonNull
	protected final TelegramBot bot() {
		return this.manager().getBot();
	}

	protected final UserData userData(@NonNull T t) {
		return UserData.of(this.type.getUserFunction().apply(t).id());
	}
}

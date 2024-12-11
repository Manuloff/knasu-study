package me.manuloff.apps.knasu.study.telegram.handler;

import com.pengrad.telegrambot.model.Message;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.manuloff.apps.knasu.study.data.UserStage;
import org.jetbrains.annotations.Nullable;

/**
 * @author Manuloff
 * @since 19:27 10.12.2024
 */
public abstract class MessageHandler extends AbstractHandler<Message> {

	@Nullable
	private final String filter;

	public MessageHandler() {
		this(null, null);
	}

	public MessageHandler(@NonNull UserStage stage) {
		this(null, stage);
	}

	public MessageHandler(@NonNull String filter) {
		this(filter, null);
	}

	public MessageHandler(@Nullable String filter, @Nullable UserStage stage) {
		super(HandlerType.MESSAGE, stage);

		this.filter = filter;
	}

	@Override
	@SneakyThrows
	protected final boolean handle(@NonNull Message update) {
		boolean result = update.text() != null && update.text().equalsIgnoreCase(this.filter);
		if (result) {
			this.handleMessage(update);
		}

		return result;
	}

	public abstract void handleMessage(@NonNull Message message);
}

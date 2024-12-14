package me.manuloff.apps.knasu.study.telegram.handler.impl.groupselection;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.CallbackHandler;
import me.manuloff.apps.knasu.study.telegram.method.DMessage;
import me.manuloff.apps.knasu.study.telegram.method.EMessage;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;
import me.manuloff.apps.knasu.study.util.DataEntry;

import java.util.List;
import java.util.UUID;

/**
 * @author Manuloff
 * @since 15:05 14.12.2024
 */
public final class GroupSelectionCallback extends CallbackHandler {
	public GroupSelectionCallback() {
		super(null, UserStage.GROUP_SELECTION);
	}

	@Override
	protected void handleCallback(@NonNull CallbackQuery callback, @NonNull DataEntry entry) {
		// 0 - выбор факультета
		// 1 - выбор года набора
		// 2 - выбор группы
		switch (entry.getInt(0)) {
			case 0 -> {
				String faculty = entry.getString(1);

				List<String> enrollmentYears = KnasuAPI.getGroups().getEnrollmentYears(faculty);
				if (enrollmentYears == null || enrollmentYears.isEmpty()) {
					this.sendErrorMessage(callback);
					return;
				}

				EMessage.of(callback).text("""
						Отлично! Ты выбрал факультет «%s». 🎓
						
						Теперь выбери год набора.
						""", faculty)
						.replyMarkup(Keyboards.enrollmentYearsKeyboard(entry, enrollmentYears)).execute();
			}
			case 1 -> {
				String faculty = entry.getString(1);
				String enrollmentYear = entry.getString(2);

				List<String> groups = KnasuAPI.getGroups().getGroupsBy(faculty, enrollmentYear);
				if (groups == null || groups.isEmpty()) {
					this.sendErrorMessage(callback);
					return;
				}

				EMessage.of(callback).text("""
						Отлично! Ты выбрал факультет «%s» и год набора «%s». 🎓
						
						Теперь выбери свою учебную группу.
						""", faculty, enrollmentYear)
						.replyMarkup(Keyboards.groupsKeyboard(groups)).execute();
			}
			case 2 -> {
				String group = entry.getString(1);

				UUID id = KnasuAPI.getGroups().getGroupIdByGroupName(group);
				if (id == null) {
					this.sendErrorMessage(callback);
					return;
				}

				UserData data = this.userData(callback);
				data.setGroup(group);
				data.setStage(UserStage.MAIN_MENU);

				this.manager().showCommandsFor(callback.from().id());

				DMessage.of(callback).execute();
				SMessage.of(callback).text("""
						Отлично! Мы нашли твою группу: «%s». 🎉
				
						Теперь ты можешь пользоваться всеми функциями бота.
						""", group)
						.replyMarkup(Keyboards.mainKeyboard()).execute();
			}
			default -> throw new IllegalStateException("Unexpected value: " + entry.getInt(0));
		}
	}

	private void sendErrorMessage(@NonNull CallbackQuery callback) {
		EMessage.of(callback).text("\uD83D\uDE15 Произошла ошибка. Попробуй ещё раз или обратись к администратору.").execute();
	}
}

package me.manuloff.apps.knasu.study.telegram.handler.impl.groupselection;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.data.UserData;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.CallbackHandler;
import me.manuloff.apps.knasu.study.telegram.method.ACallback;
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
					ACallback.of(callback)
							.text("🚨 К сожалению, произошла ошибка при получении списка годов поступления. Пожалуйста, попробуйте позже.")
							.showAlert(true)
							.execute();
					return;
				}

				EMessage.of(callback).text("""
						🎓 Вы выбрали факультет: *%s*.
						
						Теперь пожалуйста, выберите год набора. Вот доступные варианты:
						""", faculty)
						.replyMarkup(Keyboards.enrollmentYears(entry, enrollmentYears)).execute();
			}
			case 1 -> {
				String faculty = entry.getString(1);
				String enrollmentYear = entry.getString(2);

				List<String> groups = KnasuAPI.getGroups().getGroupsBy(faculty, enrollmentYear);
				if (groups == null || groups.isEmpty()) {
					ACallback.of(callback)
							.text("🚨 К сожалению, произошла ошибка при получении списка групп. Пожалуйста, попробуйте позже.")
							.showAlert(true)
							.execute();
					return;
				}

				EMessage.of(callback).text("""
						📅 Вы выбрали год набора: *%s*.
						
						Теперь пожалуйста, выберите вашу учебную группу из списка ниже:
						""", enrollmentYear)
						.replyMarkup(Keyboards.groups(groups))
						.execute();
			}
			case 2 -> {
				String group = entry.getString(1);

				UUID id = KnasuAPI.getGroups().getGroupIdByGroupName(group);
				if (id == null) {
					ACallback.of(callback)
							.text("🚨 К сожалению, произошла ошибка при получении списка групп. Пожалуйста, попробуйте позже.")
							.showAlert(true)
							.execute();
					return;
				}

				UserData data = this.userData(callback);
				boolean firstStart = data.getGroup() == null;

				data.setGroup(group);
				data.setStage(UserStage.MAIN_MENU);

				String text;

				if (firstStart) {
					this.manager().showBotCommandFor(callback.from().id());

					text = """
							✅ Вы успешно выбрали группу: *%s*.
							
							Теперь вы можете воспользоваться всеми моими возможностями. Приятного использования! 😊
							""";
				} else {
					text = """
							✅ Вы успешно выбрали группу: *%s*.
							""";
				}

				DMessage.of(callback).execute();
				SMessage.of(callback).text(text, group)
						.replyMarkup(Keyboards.mainMenu()).execute();
			}
			default -> throw new IllegalStateException("Unexpected value: " + entry.getInt(0));
		}
	}
}

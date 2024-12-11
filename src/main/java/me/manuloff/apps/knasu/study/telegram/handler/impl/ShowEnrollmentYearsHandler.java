package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.CallbackHandler;
import me.manuloff.apps.knasu.study.util.DataEntry;

import java.util.List;

/**
 * @author Manuloff
 * @since 22:14 10.12.2024
 */
public class ShowEnrollmentYearsHandler extends CallbackHandler {
	public ShowEnrollmentYearsHandler() {
		super("showEnrollmentYears", UserStage.GROUP_SELECTION);
	}

	@Override
	protected void handleCallback(@NonNull CallbackQuery callback, @NonNull DataEntry dataEntry) {
		String faculty = dataEntry.getString(1);

		List<String> enrollmentYears = KnasuAPI.getGroups().getEnrollmentYearsBy(faculty);
		if (enrollmentYears == null) {
			this.manager().answer(callback, "Ой, кажется у нас технические шоколадки, загляните к нам позже");
			return;
		}

		this.manager().edit(callback, "Так значит ты из " + faculty + ". Хорошо. Теперь выбери год набора.",
				Keyboards.enrollmentYearsKeyboard(dataEntry, enrollmentYears)
		);
	}
}

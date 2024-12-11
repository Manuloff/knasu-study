package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.CallbackHandler;
import me.manuloff.apps.knasu.study.util.DataEntry;

import java.util.UUID;

/**
 * @author Manuloff
 * @since 23:56 10.12.2024
 */
public class SetGroupHandler extends CallbackHandler {
	public SetGroupHandler() {
		super("setGroup", UserStage.GROUP_SELECTION);
	}

	@Override
	protected void handleCallback(@NonNull CallbackQuery callback, @NonNull DataEntry dataEntry) {
		String group = dataEntry.getString(3);

		UUID groupId = KnasuAPI.getGroups().getGroupIdByGroupName(group);
		if (groupId == null) {
			this.manager().answer(callback, "Ой, технические шоколадки");
			return;
		}

		userData(callback).setGroup(group);
		userData(callback).setStage(UserStage.MAIN_MENU);

		this.manager().edit(callback, "Отлично, теперь твоя группа - " + group);

		this.manager().answer(callback, "Главное меню:", Keyboards.mainKeyboard());
	}
}

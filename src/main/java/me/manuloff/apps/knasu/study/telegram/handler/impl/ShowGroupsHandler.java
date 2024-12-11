package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.GroupsResponse;
import me.manuloff.apps.knasu.study.data.UserStage;
import me.manuloff.apps.knasu.study.telegram.Keyboards;
import me.manuloff.apps.knasu.study.telegram.handler.CallbackHandler;
import me.manuloff.apps.knasu.study.util.DataEntry;

import java.util.List;

/**
 * @author Manuloff
 * @since 23:49 10.12.2024
 */
public class ShowGroupsHandler extends CallbackHandler {
	public ShowGroupsHandler() {
		super("showGroups", UserStage.GROUP_SELECTION);
	}

	@Override
	protected void handleCallback(@NonNull CallbackQuery callback, @NonNull DataEntry dataEntry) {
		String faculty = dataEntry.getString(1);
		String enrollmentYear = dataEntry.getString(2);

		GroupsResponse groupsResponse = KnasuAPI.getGroups();

		List<String> groups = groupsResponse.getGroupsBy(faculty, enrollmentYear);
		if (groups == null) {
			this.manager().answer(callback, "Ой, технические шоколадки");
			return;
		}

		this.manager().edit(callback, "Ты студент " + faculty + " " + enrollmentYear + " года набора. Вот твои группы:",
				Keyboards.groupsKeyboard(dataEntry, groups)
		);
	}
}

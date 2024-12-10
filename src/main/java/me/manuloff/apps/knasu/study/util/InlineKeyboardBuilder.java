package me.manuloff.apps.knasu.study.util;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Manuloff
 * @since 00:43 10.12.2024
 */
@UtilityClass
public class InlineKeyboardBuilder {

	public static InlineKeyboardMarkup fastText(@NonNull String... values) {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

		int i = 0;

		Iterator<String> iterator = List.of(values).iterator();

		List<InlineKeyboardButton> buttons = new LinkedList<>();

		while (iterator.hasNext()) {
			String value = iterator.next();

			buttons.add(new InlineKeyboardButton(value));

			if (i == 2) {
				markup.addRow(buttons.toArray(new InlineKeyboardButton[0]));
				buttons.clear();

				i = 0;
			} else {
				i++;
			}
		}

		if (buttons.isEmpty()) {
			markup.addRow(buttons.toArray(new InlineKeyboardButton[0]));
		}

		System.out.println("markup = " + markup);

		return markup;
	}

}

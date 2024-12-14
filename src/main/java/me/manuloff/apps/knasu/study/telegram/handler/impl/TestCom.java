package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.EditMessageMedia;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.telegram.handler.MessageHandler;

import java.io.File;

/**
 * @author Manuloff
 * @since 19:12 14.12.2024
 */
public class TestCom extends MessageHandler {

	private static final File FILE = new File("photo.jpg");

	private int messageId;

	@Override
	public void handleMessage(@NonNull Message message) {
		String text = message.text();

		switch (text) {
			case "send" -> {
				SendPhoto request = new SendPhoto(message.chat().id(), FILE).caption("Hello World!!")
						.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("1").callbackData("1")));
				SendResponse response = this.bot().execute(request);
				System.out.println("response.description() = " + response.description());
				System.out.println("response = " + response);
				this.messageId = response.message().messageId();
			}
			case "editCaption" -> {
				assert this.messageId != 0;
				EditMessageCaption request = new EditMessageCaption(message.chat().id(), this.messageId)
						.caption("Hellooo ooooo")
						.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("2").callbackData("2")));
				BaseResponse response = this.bot().execute(request);
				System.out.println("response = " + response);
			}
			case "editPhoto" -> {
				EditMessageMedia request = new EditMessageMedia(message.chat().id(), this.messageId, new InputMediaPhoto(new File("photo_1.jpg")))
						.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("3").callbackData("3")));
				BaseResponse response = this.bot().execute(request);
				System.out.println("response = " + response);
			}
		}
	}
}

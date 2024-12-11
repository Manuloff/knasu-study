package me.manuloff.apps.knasu.study.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Manuloff
 * @since 00:19 11.12.2024
 */
@RequiredArgsConstructor
public enum TextFormat {

	BOLD("**"),
	ITALIC("__"),
	MONOSPACE("`"),
	STRIKETHROUGH("~~"),
	CODE("```"),
	SPOILER("||");

	private final String symbol;

	public String format(@NonNull String text) {
		return this.symbol + text + this.symbol;
	}
}

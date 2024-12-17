package me.manuloff.apps.knasu.study.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * @author Manuloff
 * @since 16:52 11.12.2024
 */
@UtilityClass
public class CalendarUtils {

	private static final ZoneId ZONE_ID = ZoneId.of("Asia/Vladivostok");

	private static final DateTimeFormatter SHORT_FORMATTER = new DateTimeFormatterBuilder()
			.appendValue(ChronoField.DAY_OF_MONTH, 2)
			.appendLiteral('.')
			.appendValue(ChronoField.MONTH_OF_YEAR, 2)
			.toFormatter();

	private static final DateTimeFormatter FULL_FORMATTER = new DateTimeFormatterBuilder()
			.appendValue(ChronoField.DAY_OF_MONTH, 2)
			.appendLiteral('.')
			.appendValue(ChronoField.MONTH_OF_YEAR, 2)
			.appendLiteral('.')
			.appendValue(ChronoField.YEAR, 4)
			.toFormatter();

	@NonNull
	public static String getCurrentDay() {
		return LocalDate.now(ZONE_ID).format(FULL_FORMATTER);
	}

	@NonNull
	public static String getMondayOfCurrentWeek() {
		return LocalDate.now(ZONE_ID).with(DayOfWeek.MONDAY).format(FULL_FORMATTER);
	}

	@NonNull
	public static String getMondayOfWeek(@NonNull String date) {
		return LocalDate.parse(date, FULL_FORMATTER).with(DayOfWeek.MONDAY).format(FULL_FORMATTER);
	}

	@NonNull
	public static String removeYearFromDate(@NonNull String date) {
		return LocalDate.parse(date, FULL_FORMATTER).format(SHORT_FORMATTER);
	}

	@NonNull
	public static String plusDays(@NonNull String date, int days) {
		return LocalDate.parse(date, FULL_FORMATTER).plusDays(days).format(FULL_FORMATTER);
	}

	@NonNull
	public static String minusDays(@NonNull String date, int days) {
		return LocalDate.parse(date, FULL_FORMATTER).minusDays(days).format(FULL_FORMATTER);
	}

	@NonNull
	public static String getShortName(@NonNull DayOfWeek dayOfWeek) {
		return switch (dayOfWeek) {
			case MONDAY -> "Пн";
			case TUESDAY -> "Вт";
			case WEDNESDAY -> "Ср";
			case THURSDAY -> "Чт";
			case FRIDAY -> "Пт";
			case SATURDAY -> "Сб";
			case SUNDAY -> "Вс";
		};
	}
}

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
	private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
			.appendValue(ChronoField.DAY_OF_MONTH, 2)
			.appendLiteral('.')
			.appendValue(ChronoField.MONTH_OF_YEAR, 2)
			.appendLiteral('.')
			.appendValue(ChronoField.YEAR, 4)
			.toFormatter();

	@NonNull
	public static String getMondayOfCurrentWeek() {
		return LocalDate.now(ZONE_ID).with(DayOfWeek.MONDAY).format(FORMATTER);
	}
}

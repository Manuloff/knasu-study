package me.manuloff.apps.knasu.study.util;

import lombok.experimental.UtilityClass;

/**
 * @author Manuloff
 * @since 22:40 09.12.2024
 */
@UtilityClass
public class Timings {

	private static long startAt = -1;

	public static void start() {
		startAt = System.currentTimeMillis();
	}

	public static void stop() {
		if (startAt == -1) {
			throw new RuntimeException();
		}

		System.out.println((System.currentTimeMillis() - startAt) + " ms");
	}

	public static void reset() {
		if (startAt != -1) {
			stop();
		}

		start();
	}

}

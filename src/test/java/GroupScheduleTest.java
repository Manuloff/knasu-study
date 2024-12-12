import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.ScheduleResponse;
import me.manuloff.apps.knasu.study.util.CalendarUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author Manuloff
 * @since 15:04 11.12.2024
 */
public class GroupScheduleTest {

	public static void main(String[] args) {
//		GroupsResponse groups = KnasuAPI.getGroups();
//
//		String url = groups.getGroupUuidByGroupName("4ИТб-2");
//		System.out.println(url);

		UUID uuid = UUID.fromString("300ce5f8-bda8-44cf-ba78-5886688937e4");

		ScheduleResponse groupSchedule = KnasuAPI.getGroupSchedule(uuid, CalendarUtils.getMondayOfCurrentWeek());
		System.out.println("groupSchedule.getId() = " + groupSchedule.getId());
		System.out.println("groupSchedule.getDate() = " + groupSchedule.getDate());
		System.out.println("groupSchedule.getName() = " + groupSchedule.getName());
		System.out.println(groupSchedule.getDailySchedules());

	}

	public static String alignText(Map<String, String> data) {
		// Находим максимальную длину ключа
		int maxKeyLength = data.keySet().stream().mapToInt(String::length).max().orElse(0);

		StringBuilder result = new StringBuilder();

		// Форматируем каждую строку, добавляя нестандартные пробелы
		for (Map.Entry<String, String> entry : data.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			String paddedKey = padWithCustomSpace(key + ":", maxKeyLength + 5, ' ');
			result.append(paddedKey).append(value).append("\n");
		}

		return result.toString();
	}

	public static String padWithCustomSpace(String text, int targetLength, char spaceChar) {
		StringBuilder paddedText = new StringBuilder(text);
		while (paddedText.length() < targetLength) {
			paddedText.append(spaceChar);
		}
		return paddedText.toString();
	}

}

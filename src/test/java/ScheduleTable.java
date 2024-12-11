import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.GroupScheduleResponse;
import me.manuloff.apps.knasu.study.util.GroupScheduleTableRenderer;
import me.manuloff.apps.knasu.study.util.Timings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Manuloff
 * @since 18:34 11.12.2024
 */
public class ScheduleTable {


	public static void main(String[] args) throws IOException {
		Timings.reset();

		UUID id = KnasuAPI.getGroups().getGroupIdByGroupName("4ИТб-2");
		assert id != null;

		Timings.reset();

		GroupScheduleResponse groupSchedule = KnasuAPI.getGroupSchedule(id, "02.12.2024");
		Timings.reset();

		byte[] render = GroupScheduleTableRenderer.render(groupSchedule, "02.12");
		Timings.reset();

		try (FileOutputStream os = new FileOutputStream("result.png")) {
			os.write(render);
		}

		Timings.reset();
		System.out.println("Done!");

//		Timings.reset();
//
//		Table table = new Table();
//
//		table
//				.setFontName("Open Sans")
//				.setCellPadding(new Rect(20, 0))
//				.setFontSize(17)
//				.setTextAlign(TextAlign.CENTER);
//
//		table.getRow(0).setCellBackground(new Color(0xE9E9E9))
//				.setFontStyle(FontStyle.BOLD)
//				.setCellPadding(new Rect(20, 0, 20, 0))
//				.setFontSize(22);
//
//		table.getCell(0, 0).setText("№");
//		table.getCell(0, 1).setText("Пн");
//		table.getCell(0, 2).setText("Вт");
//		table.getCell(0, 3).setText("Ср");
//		table.getCell(0, 4).setText("Чт");
//		table.getCell(0, 5).setText("Пт");
//		table.getCell(0, 6).setText("Сб");
//
//		table.getRow(1).setCellBackground(new Color(0xE9E9E9))
//				.setFontStyle(FontStyle.BOLD)
//				.setCellPadding(new Rect(20, 0, 20, 0))
//				.setFontSize(25);
//
//		table.getCell(1, 0).setText("пары");
//		table.getCell(1, 1).setText("09.12");
//		table.getCell(1, 2).setText("10.12");
//		table.getCell(1, 3).setText("11.12");
//		table.getCell(1, 4).setText("12.12");
//		table.getCell(1, 5).setText("13.12");
//		table.getCell(1, 6).setText("14.12");
//
//		table.getCell(2, 0);
//		table.getCell(2, 1).setText("перенос с 30.12.24").setFontStyle(FontStyle.ITALIC).setFontColor(Color.RED);
//		table.getCell(2, 2);
//		table.getCell(2, 3);
//		table.getCell(2, 4);
//		table.getCell(2, 5);
//		table.getCell(2, 6);
//
//		table.getCell(3, 0).setText("1");
//		table.getCell(3, 1).setText("ИсР").setFontStyle(FontStyle.BOLD);
//		table.getCell(3, 2);
//		table.getCell(3, 3);
//		table.getCell(3, 4);
//		table.getCell(3, 5);
//		table.getCell(3, 6);
//
//		table.getCell(4, 0).setText("08:10 -").setFontStyle(FontStyle.BOLD);
//		table.getCell(4, 1).setText("Практические занятия (УГ-3_ПР)");
//		table.getCell(4, 2);
//		table.getCell(4, 3);
//		table.getCell(4, 4);
//		table.getCell(4, 5);
//		table.getCell(4, 6);
//
//		table.getCell(5, 0).setText("09:40").setFontStyle(FontStyle.BOLD);
//		table.getCell(5, 1).setText("Аксенов А.А.").setFontColor(new Color(0x3EB7EF));
//		table.getCell(5, 2);
//		table.getCell(5, 3);
//		table.getCell(5, 4);
//		table.getCell(5, 5);
//		table.getCell(5, 6);
//
//		table.getCell(6, 0);
//		table.getCell(6, 1).setText("403/1").setFontStyle(FontStyle.BOLD);
//		table.getCell(6, 2);
//		table.getCell(6, 3);
//		table.getCell(6, 4);
//		table.getCell(6, 5);
//		table.getCell(6, 6);
//
//		Timings.reset();
//
//		File file = new File("test.png");
//		table.savePng(new FileOutputStream(file));
//
//		Timings.reset();
	}

}

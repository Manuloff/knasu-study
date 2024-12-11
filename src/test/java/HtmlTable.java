import org.xhtmlrenderer.simple.Graphics2DRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author Manuloff
 * @since 21:02 11.12.2024
 */
public class HtmlTable {

	public static void main(String[] args) throws IOException {
		String content = "<table class=\"table table-bordered table-print col-md-12 schedule\"><thead><tr><th class=\"\" style=\"background: #e9e9e9; text-align: center;\">№<br>пары</th><th title=\"\" class=\"\" style=\"background: #e9e9e9; text-align: center;\">Пн<br>09.12</th><th title=\"\" class=\"\" style=\"background: #e9e9e9; text-align: center;\">Вт<br>10.12</th><th title=\"Выбранная дата\n" +
				"\" class=\"today bold\" style=\"background: #e9e9e9; text-align: center;\">Ср<br>11.12</th><th title=\"\" class=\"\" style=\"background: #e9e9e9; text-align: center;\">Чт<br>12.12</th><th title=\"\" class=\"\" style=\"background: #e9e9e9; text-align: center;\">Пт<br>13.12</th><th title=\"\" class=\"\" style=\"background: #e9e9e9; text-align: center;\">Сб<br>14.12</th></tr></thead><tbody><tr skip=\"0\"><td style=\"text-align: center; vertical-align: middle;\">1<br><b>08:10 - <br>09:40</b></td><td style=\"text-align: center; vertical-align: middle;\" title=\"\" class=\"\"><div class=\"table-print-inner-container\"><div class=\"table-print-inner-event col-sm-12 col-md-12\"><i class=\"\" style=\"color:red;\" title=\"Перенос с 30.12.24\">Перенос с 30.12.24</i><br><b data-toggle=\"tooltip\" title=\"История России\">ИсР</b><br>Практические занятия (УГ-3_ПР)<br><a title=\"Расписание преподавателя\" target=\"_blank\" href=\"/teachers/schedule/3567\">Аксенов А.А.</a>&nbsp;<br><b>403/1</b><div data-prof_id=\"3567\" data-lesson_uid=\"5a1d4ee3-3bc2-4c44-9595-89716081abd2\" class=\"edit_event\">?</div></div></div></td><td title=\"\" class=\"\"></td><td title=\"Выбранная дата\" class=\"today bold\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td></tr><tr skip=\"0\"><td style=\"text-align: center; vertical-align: middle;\">2<br><b>09:50 - <br>11:20</b></td><td style=\"text-align: center; vertical-align: middle;\" title=\"\" class=\"\"><div class=\"table-print-inner-container\"><div class=\"table-print-inner-event col-sm-12 col-md-12\"><b data-toggle=\"tooltip\" title=\"История России\">ИсР</b><br>Лекции<br><a title=\"Расписание преподавателя\" target=\"_blank\" href=\"/teachers/schedule/3567\">Аксенов А.А.</a>&nbsp;<br><b>403/1</b><div data-prof_id=\"3567\" data-lesson_uid=\"276c6c4f-ecaf-49f7-9ba6-5243f610f622\" class=\"edit_event\">?</div></div></div></td><td style=\"text-align: center; vertical-align: middle;\" title=\"\" class=\"\"><div class=\"table-print-inner-container\"><div class=\"table-print-inner-event col-sm-12 col-md-12\"><b data-toggle=\"tooltip\" title=\"Линейная алгебра и аналитическая геометрия\">ЛАиАГ</b><br>Практические занятия (УГ-2_ПР)<br><a title=\"Расписание преподавателя\" target=\"_blank\" href=\"/teachers/schedule/4710\">Чернышова Д.В.</a>&nbsp;<br><b>312/1</b><div data-prof_id=\"4710\" data-lesson_uid=\"5170fd69-5f43-47b3-a159-2e10f1f89b68\" class=\"edit_event\">?</div></div></div></td><td title=\"Выбранная дата\" class=\"today bold\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td></tr><tr skip=\"0\"><td style=\"text-align: center; vertical-align: middle;\">3<br><b>11:30 - <br>13:00</b></td><td style=\"text-align: center; vertical-align: middle;\" title=\"\" class=\"\"><div class=\"table-print-inner-container\"><div class=\"table-print-inner-event col-sm-12 col-md-12\"><b data-toggle=\"tooltip\" title=\"История России\">ИсР</b><br>Практические занятия (УГ-3_ПР)<br><a title=\"Расписание преподавателя\" target=\"_blank\" href=\"/teachers/schedule/3567\">Аксенов А.А.</a>&nbsp;<br><b>403/1</b><div data-prof_id=\"3567\" data-lesson_uid=\"f372f2d5-e31a-481a-bf0f-35002443cf42\" class=\"edit_event\">?</div></div></div></td><td title=\"\" class=\"\"></td><td style=\"text-align: center; vertical-align: middle;\" title=\"Выбранная дата\" class=\"today\" bold=\"\"><div class=\"table-print-inner-container\"><div class=\"table-print-inner-event col-sm-12 col-md-12\"><i class=\"\" style=\"color:red;\" title=\"перенос в связи с каникулярной школой\">перенос в связи с каникулярной школой</i><br><b data-toggle=\"tooltip\" title=\"Введение в профессиональную деятельность\">ВведПрофД</b><br>Практические занятия (УГ-3_ПР)<br><a title=\"Расписание преподавателя\" target=\"_blank\" href=\"/teachers/schedule/5623\">Гулина Н.А.</a>&nbsp;<br><b>205/5</b><div data-prof_id=\"5623\" data-lesson_uid=\"be7f39fd-f7d3-4965-b4f1-fcb231bfbcb7\" class=\"edit_event\">?</div></div></div></td><td title=\"\" class=\"\"></td><td style=\"text-align: center; vertical-align: middle;\" title=\"\" class=\"\"><div class=\"table-print-inner-container\"><div class=\"table-print-inner-event col-sm-12 col-md-12\"><b data-toggle=\"tooltip\" title=\"Математический анализ\">МатАН</b><br>Практические занятия (УГ-2_ПР)<br><a title=\"Расписание преподавателя\" target=\"_blank\" href=\"/teachers/schedule/3918\">Сташкевич М.В.</a>&nbsp;<br><b>301/3</b><div data-prof_id=\"3918\" data-lesson_uid=\"44b03949-f9bf-4b8e-9387-ec2fb6dfdf04\" class=\"edit_event\">?</div></div></div></td><td title=\"\" class=\"\"></td></tr><tr skip=\"0\"><td style=\"text-align: center; vertical-align: middle;\">4<br><b>13:30 - <br>15:00</b></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"Выбранная дата\" class=\"today bold\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td></tr><tr skip=\"0\"><td style=\"text-align: center; vertical-align: middle;\">5<br><b>15:10 - <br>16:40</b></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"Выбранная дата\" class=\"today bold\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td></tr><tr skip=\"0\"><td style=\"text-align: center; vertical-align: middle;\">6<br><b>16:50 - <br>18:20</b></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"Выбранная дата\" class=\"today bold\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td></tr><tr skip=\"0\"><td style=\"text-align: center; vertical-align: middle;\">7<br><b>18:30 - <br>20:00</b></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"Выбранная дата\" class=\"today bold\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td></tr><tr skip=\"0\"><td style=\"text-align: center; vertical-align: middle;\">8<br><b>20:10 - <br>21:40</b></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"Выбранная дата\" class=\"today bold\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td><td title=\"\" class=\"\"></td></tr></tbody></table>";

		byte[] bytes = htmlToPngBytes(content, 1000, 1000);

		try (FileOutputStream stream = new FileOutputStream("image.png")) {
			stream.write(bytes);
		}
	}

	public static byte[] htmlToPngBytes(String html, int width, int height) throws IOException {

		final BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration()
				.createCompatibleImage(width, height);

		final JEditorPane pane = new JEditorPane("text/html", html);
		final Graphics2D graphics = image.createGraphics();

		graphics.setBackground(new Color(0f, 1f, 0f, .5f));
//        graphics.setBackground(null);

		pane.setSize(width, height);
		pane.setOpaque(false);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setBackground(new Color(1f, 0f, 0f, .5f));
		pane.print(graphics);

		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ImageIO.write(image, "png", baos);

			return baos.toByteArray();
		}
	}

	public static InputStream htmlToPng(String html, int width, int height) throws IOException {
		return new ByteArrayInputStream(htmlToPngBytes(html, width, height));
	}

}

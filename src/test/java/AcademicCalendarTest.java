import me.manuloff.apps.knasu.study.api.KnasuAPI;

/**
 * @author Manuloff
 * @since 01:38 14.12.2024
 */
public class AcademicCalendarTest {

	public static void main(String[] args) {
		String href = "/Alfresco/get_document?nodeRef=workspace://SpacesStore/098c99f3-8656-4d7c-bdee-169efb6fdf48&name=КУГ%201БЛб-1%2045.03.02%20П.pdf";

		System.out.println("KnasuAPI.getAcademicCalendar(href) = " + KnasuAPI.getAcademicCalendar(href));
	}

}

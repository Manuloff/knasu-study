import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.GroupProgramAnnotation;

/**
 * @author Manuloff
 * @since 19:38 13.12.2024
 */
public class WorkingProgramTest {

	public static void main(String[] args) {
		String path = "/Alfresco/get_document?nodeRef=workspace://SpacesStore/b2a437b0-c3a4-47cb-96cc-24511429d0fb&name=Аннотация Информационные технологии\t.pdf";

		GroupProgramAnnotation annotation = KnasuAPI.getGroupProgramAnnotation(path);
	}

}

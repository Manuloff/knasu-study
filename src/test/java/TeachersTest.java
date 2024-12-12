import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.TeachersResponse;

import java.util.List;

/**
 * @author Manuloff
 * @since 01:49 11.12.2024
 */
public class TeachersTest {

	public static void main(String[] args) {
		TeachersResponse teachers = KnasuAPI.getTeachers();

		List<TeachersResponse.Teacher> query = teachers.getByQuery("Сташкевич");

	}

}

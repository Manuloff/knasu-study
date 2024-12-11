import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.TeachersResponse;
import me.manuloff.apps.knasu.study.util.Timings;

import java.util.List;

/**
 * @author Manuloff
 * @since 01:49 11.12.2024
 */
public class TeachersTest {

	public static void main(String[] args) {
		TeachersResponse teachers = KnasuAPI.getTeachers();

		System.out.println(teachers.getByQuery("Сташкевич"));

	}

}

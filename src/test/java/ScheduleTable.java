import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.ScheduleResponse;
import me.manuloff.apps.knasu.study.api.response.TeachersResponse;
import me.manuloff.apps.knasu.study.renderer.ScheduleTableRenderer;
import me.manuloff.apps.knasu.study.util.Timings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Manuloff
 * @since 18:34 11.12.2024
 */
public class ScheduleTable {


	public static void main(String[] args) throws IOException {
		Timings.reset();

		TeachersResponse teachers = KnasuAPI.getTeachers();
		List<TeachersResponse.Teacher> query = teachers.getByQuery("Трещев Иван Андреевич");
		assert !query.isEmpty();

		TeachersResponse.Teacher teacher = query.get(0);

//		UUID id = KnasuAPI.getGroups().getGroupIdByGroupName("2ИТб-2");
//		assert id != null;

		Timings.reset();

		ScheduleResponse schedule = KnasuAPI.getTeacherSchedule(teacher.getId(), "25.11.2024");
		Timings.reset();

		byte[] render = ScheduleTableRenderer.render(schedule, null);
		Timings.reset();

		try (FileOutputStream os = new FileOutputStream("result.png")) {
			os.write(render);
		}

		Timings.reset();
		System.out.println("Done!");
	}

}

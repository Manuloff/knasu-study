import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.ScheduleResponse;
import me.manuloff.apps.knasu.study.api.response.TeachersResponse;
import me.manuloff.apps.knasu.study.util.CalendarUtils;

import java.util.List;

/**
 * @author Manuloff
 * @since 12:10 12.12.2024
 */
public class TeacherScheduleTest {

	public static void main(String[] args) {
		TeachersResponse teachers = KnasuAPI.getTeachers();
		List<TeachersResponse.Teacher> query = teachers.getByQuery("Трещев Иван Андреевич");
		assert !query.isEmpty();

		TeachersResponse.Teacher teacher = query.get(0);

		ScheduleResponse schedule = KnasuAPI.getTeacherSchedule(teacher.getId(), CalendarUtils.getMondayOfCurrentWeek());

		System.out.println("schedule.getId() = " + schedule.getId());
		System.out.println("schedule.getDate() = " + schedule.getDate());
		System.out.println("schedule.getName() = " + schedule.getName());
		System.out.println("schedule.getDailySchedules() = " + schedule.getDailySchedules());
	}

}

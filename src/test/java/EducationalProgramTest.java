import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.EducationalProgramResponse;
import me.manuloff.apps.knasu.study.api.response.WorkingStudyPlanResponse;

/**
 * @author Manuloff
 * @since 22:52 12.12.2024
 */
public class EducationalProgramTest {

	public static void main(String[] args) {
		String code = KnasuAPI.getGroupCodes().getCodeByName("4ИТб-2");
		assert code != null;

		String url = KnasuAPI.getEducationalProgram(code).getWorkingStudyPlanUrl("4ИТб-2");
		assert url != null;

		WorkingStudyPlanResponse workingProgram = KnasuAPI.getWorkingProgram(url);

		System.out.println("workingProgram.getPeriods() = " + workingProgram.getPeriods());
		System.out.println("workingProgram.getStateExam() = " + workingProgram.getStateExam());
	}

}

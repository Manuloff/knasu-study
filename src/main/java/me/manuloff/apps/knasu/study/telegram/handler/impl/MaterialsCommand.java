package me.manuloff.apps.knasu.study.telegram.handler.impl;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.NonNull;
import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.WorkingStudyPlanResponse;
import me.manuloff.apps.knasu.study.telegram.handler.CommandHandler;
import me.manuloff.apps.knasu.study.telegram.method.EMessage;
import me.manuloff.apps.knasu.study.telegram.method.SMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Manuloff
 * @since 23:12 15.12.2024
 */
public class MaterialsCommand extends CommandHandler {
	public MaterialsCommand() {
		super("/materials", "📋 Полезные материалы");
	}

	@Override
	public void handleCommand(@NonNull Message message) {
		long userId = message.from().id();

		int messageId = SMessage.of(message).text("""
				⏳ Пожалуйста, подождите немного, пока загружается информация о вашей учебной группе.
				""").execute().message().messageId();

		String group = Objects.requireNonNull(userData(message).getGroup());

		String specialityCode = KnasuAPI.getGroupCodes().getCodeByName(group);
		if (specialityCode == null) {
			EMessage.of(userId, messageId).text("""
					❌ Не удалось найти код специальности для вашей учебной группы.
					""").execute();
			return;
		}

		String workingStudyPlanUrl = KnasuAPI.getEducationalProgram(specialityCode).getWorkingStudyPlanUrl(group);
		WorkingStudyPlanResponse workingStudyPlanResponse = workingStudyPlanUrl != null
				? KnasuAPI.getWorkingProgram(workingStudyPlanUrl) : null;

		String departmentMaterials = null;

		String facultyName = KnasuAPI.getGroups().getFacultyByGroup(group);
		if (facultyName != null) {
			String facultyUrl = KnasuAPI.getFaculties().getUrl(facultyName);
			if (facultyUrl != null) {
				String specialtiesUrl = KnasuAPI.getFaculty(facultyUrl).getSpecialtiesUrl();
				if (specialtiesUrl != null) {
					String departmentUrl = KnasuAPI.getFacultySpecialties(specialtiesUrl).getDepartmentBySpecialityCode(specialityCode);
					if (departmentUrl != null) {
						departmentMaterials = KnasuAPI.getDepartment(departmentUrl).getMethodicalMaterials();
					}
				}
			}
		}

		List<InlineKeyboardButton> buttons = new LinkedList<>();
		List<String> materials = new LinkedList<>();

		if (workingStudyPlanResponse != null) {
			if (workingStudyPlanResponse.getMethodicalMaterials() != null) {
				materials.add("""
						*Методические материалы направления* - содержат рекомендации и инструкции для подготовки к практическим занятиям, выполнению рефератов, курсовых работ, научных докладов и других учебных заданий.
						""");
				buttons.add(new InlineKeyboardButton("Методические материалы направления").url("https://knastu.ru" + workingStudyPlanResponse.getMethodicalMaterials()));
			}

			if (workingStudyPlanResponse.getCompetencyPassport() != null) {
				materials.add("""
						*Паспорт компетенций* - документ, подробно описывающий навыки, умения и знания, которые студенты должны освоить в рамках образовательной программы. В нем указаны требования к итоговой аттестации и ожидаемые результаты обучения.
						""");
				buttons.add(new InlineKeyboardButton("Паспорт компетенций").url("https://knastu.ru" + workingStudyPlanResponse.getCompetencyPassport()));
			}

			if (workingStudyPlanResponse.getLiteratureRegistry() != null) {
				materials.add("""
						*Реестр литературы* - включает список рекомендованных учебных и научных изданий, необходимых для изучения дисциплин. В реестре представлены книги, статьи и учебные пособия, которые помогут углубить знания по предметам.
						""");
				buttons.add(new InlineKeyboardButton("Реестр литературы").url("https://knastu.ru" + workingStudyPlanResponse.getLiteratureRegistry()));
			}

			if (workingStudyPlanResponse.getEbsRegistry() != null) {
				materials.add("""
						*Реестр ЭБС* _(электронных библиотечных систем)_ - содержит ссылки и доступы к цифровым библиотекам, где можно найти учебную литературу, статьи, монографии и другие ресурсы, которые поддерживают образовательный процесс.
						""");
				buttons.add(new InlineKeyboardButton("Реестр ЭБС").url("https://knastu.ru" + workingStudyPlanResponse.getEbsRegistry()));
			}

			if (workingStudyPlanResponse.getSoftwareRegistry() != null) {
				materials.add("""
						*Реестр ПО* _(программного обеспечения)_ - список необходимого софта и инструментов, используемых для практических занятий и лабораторных работ.
						""");
				buttons.add(new InlineKeyboardButton("Реестр ПО").url("https://knastu.ru" + workingStudyPlanResponse.getSoftwareRegistry()));
			}
		}

		if (departmentMaterials != null) {
			materials.add("""
						*Методические материалы кафедры* - дополнительные материалы и указания, разработанные непосредственно вашей кафедрой. Они могут включать примеры работ, требования к оформлению и советы для успешного выполнения учебных заданий и проектов.
						""");
			buttons.add(new InlineKeyboardButton("Методические материалы кафедры").url("https://knastu.ru" + departmentMaterials));
		}

		String[] emojiNumbers = new String[] {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣"};

		InlineKeyboardMarkup markup;
		StringBuilder builder = new StringBuilder();

		if (materials.isEmpty()) {
			builder.append("⚠️ Я не смог найти полезные материалы для вас. Приношу свои извинения.");
			markup = null;

		} else {
			markup = new InlineKeyboardMarkup();

			builder.append("✅ Я смог найти следующие полезные материалы для вас:").append('\n').append('\n');

			for (int i = 0; i < materials.size(); i++) {
				String emojiNumber = emojiNumbers[i];
				String material = materials.get(i);

				builder.append(emojiNumber).append(' ').append(material).append('\n');

				markup.addRow(buttons.get(i));
			}

			builder.append("🔽 Интересующие вас материалы вы можете открыть с помощью соответствующей кнопки снизу.");
		}

		EMessage eMessage = EMessage.of(userId, messageId).text(builder.toString());
		if (markup != null) {
			eMessage.replyMarkup(markup);
		}

		eMessage.execute();
	}
}

package me.manuloff.apps.knasu.study.api.response;

import lombok.Getter;
import lombok.NonNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Manuloff
 * @since 03:27 17.12.2024
 */
@Getter
public class FacultyResponse {

	private final String specialtiesUrl;

	public FacultyResponse(@NonNull Document document) {
		Element sideMenu = document.body().selectFirst("ul#sub_menu.side-menu.nav");
		if (sideMenu != null) {
			Element specialities = sideMenu.getElementsContainingOwnText("Направления и специальности").first();
			if (specialities != null) {
				this.specialtiesUrl = specialities.attr("href");
				return;
			}
		}
		this.specialtiesUrl = null;
	}

}

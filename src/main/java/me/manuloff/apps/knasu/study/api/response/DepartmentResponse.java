package me.manuloff.apps.knasu.study.api.response;

import lombok.Getter;
import lombok.NonNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Manuloff
 * @since 12:19 17.12.2024
 */
@Getter
public class DepartmentResponse {

	private final String methodicalMaterials;

	public DepartmentResponse(@NonNull Document document) {
		Element sideMenu = document.selectFirst("ul#sub_menu.side-menu.nav");
		if (sideMenu != null) {
			Element methodicalMaterials = sideMenu.getElementsContainingOwnText("Методические материалы").first();
			if (methodicalMaterials != null) {
				this.methodicalMaterials = methodicalMaterials.attr("href");
				return;
			}
		}

		this.methodicalMaterials = null;
	}

}

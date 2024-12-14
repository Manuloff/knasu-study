package me.manuloff.apps.knasu.study.api.response;

import lombok.NonNull;
import lombok.SneakyThrows;
import me.manuloff.apps.knasu.study.KnasuStudy;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Manuloff
 * @since 14:10 12.12.2024
 */
public class GroupCodesResponse {

	private static final Pattern PATTERN = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{2}).*?(\\d{1,2}[А-Яа-яA-Za-z0-9]{2,6}(\\([а-яА-Я]\\))?-\\d{1,2}[А-Яа-яA-Za-z]?)");

	private final Map<String, String> codeByName = new LinkedHashMap<>();

	@SneakyThrows
	public GroupCodesResponse(@NonNull Document document) {
		Elements body = document.body().select("div.col-md-12.main_text");

		Element spoiler = body.select("div.spoiler").stream()
				.filter(element -> {
					return element.select("div.division span.division_name").text()
							.equalsIgnoreCase("Календарный учебный график");
				}).findFirst().orElse(null);

		if (spoiler == null) return;

		String url = "https://knastu.ru" + Objects.requireNonNull(spoiler.select("li a").last()).attr("href");

		try (InputStream in = new URL(url).openConnection().getInputStream()) {
			try (PDDocument doc = PDDocument.load(in)) {
				PDFTextStripper stripper = new PDFTextStripper();

				stripper.setSortByPosition(true);

				stripper.setStartPage(1);
				stripper.setEndPage(doc.getNumberOfPages());

				String text = stripper.getText(doc);

				Matcher matcher = PATTERN.matcher(text);

				while (matcher.find()) {
					this.codeByName.put(matcher.group(2), matcher.group(1));
				}
			}
		}

	}

	@Nullable
	public String getCodeByName(@NonNull String name) {
		return this.codeByName.getOrDefault(name, KnasuStudy.getInstance().getAppConfig().getGroupCodes().get(name));
	}

}

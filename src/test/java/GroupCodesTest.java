import me.manuloff.apps.knasu.study.api.KnasuAPI;
import me.manuloff.apps.knasu.study.api.response.GroupCodesResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Manuloff
 * @since 14:15 12.12.2024
 */
public class GroupCodesTest {

	private static final Pattern PATTERN = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{2}).*?(\\d{1,2}[А-Яа-яA-Za-z0-9]{2,6}(\\([а-яА-Я]\\))?-\\d{1,2}[А-Яа-яA-Za-z]?)");

	public static void main(String[] args) throws Exception {
		URL url = new URL("https://knastu.ru/media/files/page_files/students/Prikaz_ot_23.05.2024_%E2%84%96_160-O_KUG_s_prilozheniyem.pdf");

		Map<String, String> map = new LinkedHashMap<>();

		try (InputStream in = url.openConnection().getInputStream()) {
			try (PDDocument document = PDDocument.load(in)) {
				PDFTextStripper stripper = new PDFTextStripper();

				stripper.setSortByPosition(true);

				stripper.setStartPage(1);
				stripper.setEndPage(document.getNumberOfPages());

				String text = stripper.getText(document);

				System.out.println("text = " + text);

				Matcher matcher = PATTERN.matcher(text);

				while (matcher.find()) {
					String code = matcher.group(1);
					String name = matcher.group(2);

					map.put(name, code);
				}
			}
		}

		for (String group : KnasuAPI.getGroups().getAllGroups()) {
			System.out.println(group + " - " + map.get(group));
		}
	}

}

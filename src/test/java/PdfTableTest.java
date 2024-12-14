import lombok.NonNull;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * @author Manuloff
 * @since 13:01 12.12.2024
 */
public class PdfTableTest {

	public static void main(String[] args) throws Exception {
		URL url = new URL("https://knastu.ru/Alfresco/get_document?nodeRef=workspace://SpacesStore/f982438b-b5dc-4fa8-b0e5-a7429650dc96&name=%D0%9A%D0%A3%D0%93%204%D0%98%D0%A2%D0%B1-2%20%D0%98%D0%A1%2009.03.02.pdf");

		URLConnection connection = url.openConnection();

		try (InputStream in = connection.getInputStream()) {
			System.out.println("Saved to input stream");

			processPdfWithTabula(in);
		}
	}

	private static void processPdfWithTabula(@NonNull InputStream in) throws Exception {
		SpreadsheetExtractionAlgorithm algorithm = new SpreadsheetExtractionAlgorithm();

		try (PDDocument document = PDDocument.load(in)) {
			ObjectExtractor extractor = new ObjectExtractor(document);

			PageIterator iterator = extractor.extract();

			label1:
			while (iterator.hasNext()) {
				Page page = iterator.next();

				System.out.println("page.getPageNumber() = " + page.getPageNumber());

				for (Table table : algorithm.extract(page)) {
					System.out.println("table = " + table);

					if (!table.getCell(0, 0).getText().equalsIgnoreCase("Мес")) {
						System.out.println("skip");
						continue;
					}

					for (int i = 0; i < table.getRowCount(); i++) {
						System.out.println("table.getCell(i, 1).getText() = " + table.getCell(i, 1));
					}

//					for (int i = 0; i < table.getColCount(); i++) {
//						System.out.println("table.getCell(0, i) = " + table.getCell(0, i));
//					}

					break label1;
				}
			}
		}
	}

}

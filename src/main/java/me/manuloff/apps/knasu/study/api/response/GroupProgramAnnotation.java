package me.manuloff.apps.knasu.study.api.response;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Manuloff
 * @since 19:40 13.12.2024
 */
@Log4j2
@Getter
public class GroupProgramAnnotation {

	private static final SpreadsheetExtractionAlgorithm ALGORITHM = new SpreadsheetExtractionAlgorithm();

	private final List<byte[]> images = new LinkedList<>();

	@SneakyThrows
	public GroupProgramAnnotation(@NonNull PDDocument document) {
		PDFRenderer renderer = new PDFRenderer(document);
		PageIterator iterator = new ObjectExtractor(document).extract();

		int lastRememberedOffset = -1;

		while (iterator.hasNext()) {
			Page page = iterator.next();

			BufferedImage fullImage = renderer.renderImage(page.getPageNumber() - 1);

			List<Table> tables = ALGORITHM.extract(page);

			List<byte[]> imagesFromPage = new LinkedList<>();

			for (int i = tables.size() - 1; i >= 0; i--) {
				Table table = tables.get(i);

				Rectangle bounds = table.getBounds();

				int offset;
				if (i - 1 >= 0) {
					Table nextTable = tables.get(i - 1);
					offset = (int) (bounds.getY() - nextTable.getMaxY());
					lastRememberedOffset = offset;
				} else {
					offset = lastRememberedOffset != -1 ? lastRememberedOffset : 30;
				}

				try {
					BufferedImage tableWithHeader = fullImage.getSubimage(bounds.x - 2, bounds.y - offset, bounds.width + 4, bounds.height + offset + 2);

					try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
						ImageIO.write(tableWithHeader, "png", out);

						imagesFromPage.add(out.toByteArray());
					}
				} catch (Exception e) {

				}
			}

			Collections.reverse(imagesFromPage);

			this.images.addAll(imagesFromPage);
		}
	}
}

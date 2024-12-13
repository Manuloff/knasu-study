package me.manuloff.apps.knasu.study.api.response;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Manuloff
 * @since 00:39 14.12.2024
 */
@Getter
public class AcademicCalendarResponse {

	/*
	На 14.12.2024 существует 3 типа календарного учебного графика:
	1. Самый актуальный, содержится у всех (это не точно) групп с началом обучения от 2022 года, иногда встречается в 2021 и 2020.
		PDF файл состоит из n количества таблиц, каждая из которых обозначает 1 учебный год.
	2. Старый, встречался у групп с началом подготовки 2021 года. Предоставлена 1 таблица, содержащая в себе информация по всем годам сразу.
	 	Помимо основной таблицы, существует n количество других одинаковых по структуру, каждая из них обозначает 1 учебный год.
	3. Древний, встречался у групп 2020 года. Картинки...
	 */

	private static final SpreadsheetExtractionAlgorithm ALGORITHM = new SpreadsheetExtractionAlgorithm();

	private final List<BufferedImage> images = new LinkedList<>();

	@SneakyThrows
	public AcademicCalendarResponse(@NonNull PDDocument document) {
		PDFRenderer renderer = new PDFRenderer(document);
		PageIterator iterator = new ObjectExtractor(document).extract();

		while (iterator.hasNext()) {
			Page page = iterator.next();

			BufferedImage bufferedImage = renderer.renderImage(page.getPageNumber() - 1);

			for (Table table : ALGORITHM.extract(page)) {
				if (table.getRowCount() < 10 || table.getColCount() < 45) {
					continue;
				}

				Rectangle bounds = table.getBounds();
				BufferedImage tableImage = bufferedImage.getSubimage(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6);

				this.images.add(tableImage);
			}
		}
	}

}

package me.manuloff.apps.knasu.study.renderer;

import com.suke.jtable.Cell;
import com.suke.jtable.Table;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Manuloff
 * @since 12:19 12.12.2024
 */
public abstract class AbstractTableRenderer {

	protected final Table table;

	protected int currentRow;
	protected int currentCol;

	protected AbstractTableRenderer() {
		this.table = new Table();
	}

	protected final Cell cell() {
		return this.table.getCell(this.currentRow++, this.currentCol);
	}

	protected final Cell cell(int colSpan) {
		return this.table.getCell(this.currentRow++, this.currentCol, 1, colSpan);
	}

	protected final void nextColumn() {
		this.currentCol++;
		this.currentRow = 0;
	}

	@SneakyThrows
	protected final byte[] render() {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			this.table.savePng(out);
			byte[] bytes = out.toByteArray();

			return padToSquare(bytes);
		}
	}

	@SneakyThrows
	private byte[] padToSquare(byte[] bytes) {
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			BufferedImage image = ImageIO.read(in);

			int width = image.getWidth();
			int height = image.getHeight();

			int squareSize = Math.max(width, height);

			BufferedImage squareImage = new BufferedImage(squareSize, squareSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = squareImage.createGraphics();

			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, squareSize, squareSize);

			int x = (squareSize - width) / 2;
			int y = (squareSize - height) / 2;

			g2.drawImage(image, x, y, null);
			g2.setColor(Color.BLACK);

			int borderWidth = 2;

			g2.setStroke(new BasicStroke(borderWidth));
			g2.drawRect(x - borderWidth / 2, y - borderWidth / 2, width + borderWidth, height + borderWidth);
			g2.dispose();

			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				ImageIO.write(squareImage, "png", out);
				return out.toByteArray();
			}
		}
	}

}

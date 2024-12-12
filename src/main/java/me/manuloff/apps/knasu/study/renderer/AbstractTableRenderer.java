package me.manuloff.apps.knasu.study.renderer;

import com.suke.jtable.Cell;
import com.suke.jtable.Table;
import lombok.SneakyThrows;

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
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			this.table.savePng(baos);

			return baos.toByteArray();
		}
	}

}

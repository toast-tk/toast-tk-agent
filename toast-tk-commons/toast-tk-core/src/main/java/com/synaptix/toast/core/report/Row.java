package com.synaptix.toast.core.report;

import java.util.ArrayList;
import java.util.List;

class Row {
	private List<Cell> cells;
	private CellColor color;

	public Row() {
		setColor(CellColor.NONE);
		cells = new ArrayList<Cell>();
	}

	public Row(List<String> cells) {
		this();
		setCells(cells);
	}

	public Row(Cell cell) {
		this();
		cells.add(cell);
	}

	public void setCells(List<String> cells) {
		for (String string : cells) {
			this.cells.add(new Cell(string));
		}
	}

	public List<Cell> getCells() {
		return cells;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("|");
		for (Cell cell : cells) {
			stringBuilder.append(cell.toString());
			stringBuilder.append("|");
		}
		stringBuilder.append("(" + this.color + ")");
		return stringBuilder.toString();
	}

	public CellColor getColor() {
		return color;
	}

	public void setColor(CellColor color) {
		this.color = color;
	}

	public Cell getCellAt(int i) {
		return cells.get(i);
	}

	public void addCell(String string, CellColor color) {
		cells.add(new Cell(string, color));
	}

	public void addCell(String string) {
		cells.add(new Cell(string, CellColor.NONE));
	}
}

package com.synaptix.toast.core.report;

class Cell {
	private String value;
	private CellColor color;

	public Cell() {
		setValue(null);
		setColor(CellColor.NONE);
	}

	public Cell(String value) {
		this();
		this.setValue(value);
	}

	public Cell(String value, CellColor color) {
		this.value = value;
		this.color = color;
	}

	@Override
	public String toString() {
		return getValue() + "(" + getColor().toString() + ")";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public CellColor getColor() {
		return color;
	}

	public void setColor(CellColor color) {
		this.color = color;
	}
}

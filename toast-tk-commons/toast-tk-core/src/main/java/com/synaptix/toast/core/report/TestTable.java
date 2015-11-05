package com.synaptix.toast.core.report;

import java.util.ArrayList;
import java.util.List;

class TestTable {

	private Row header;

	private List<Row> rows;

	public TestTable() {
		setHeader(new Row());
		rows = new ArrayList<Row>();
	}

	public void setHeader(
		List<String> headerValues) {
		header.setCells(headerValues);
	}

	public void addRow(
		List<String> cells) {
		rows.add(new Row(cells));
	}

	public void addRow(
		Row row) {
		rows.add(row);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getHeader().toString());
		stringBuilder.append("\n");
		for(Row row : rows) {
			stringBuilder.append(row.toString());
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}

	public String toHtml() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<table border=\"1\"><tr>");
		stringBuilder.append("<tr>");
		for(Cell headerCell : this.header.getCells()) {
			stringBuilder.append("<th " + getHtmlColor(headerCell.getColor()) + ">");
			stringBuilder.append(headerCell.getValue());
			stringBuilder.append("</th>");
		}
		stringBuilder.append("</tr>");
		for(Row row : this.getRows()) {
			stringBuilder.append("<tr " + getHtmlColor(row.getColor()) + ">");
			for(Cell cell : row.getCells()) {
				if(CellColor.NONE.equals(row.getColor())) {
					stringBuilder.append("<td>");
				}
				else {
					stringBuilder.append("<td " + getHtmlColor(cell.getColor()) + ">");
				}
				stringBuilder.append(cell.getValue());
				stringBuilder.append("</td>");
			}
			stringBuilder.append("</tr>");
		}
		stringBuilder.append("</tr></table> ");
		return stringBuilder.toString();
	}

	private String getHtmlColor(
		CellColor color) {
		if(CellColor.NONE.equals(color)) {
			return "";
		}
		else {
			return "bgcolor=\"" + getColor(color) + "\"";
		}
	}

	private String getColor(
		CellColor color) {
		if(CellColor.GREEN.equals(color)) {
			return "#6FE436";
		}
		else if(CellColor.RED.equals(color)) {
			return "#F1395E";
		}
		else if(CellColor.YELLOW.equals(color)) {
			return "#FF833D";
		}
		else if(CellColor.BLUE.equals(color)) {
			return "#2CBA93";
		}
		else {
			return "White";
		}
	}

	public Row getHeader() {
		return header;
	}

	public void setHeader(
		Row header) {
		this.header = header;
	}

	public List<Row> getRows() {
		return rows;
	}

	public Row getRowAt(
		int i) {
		return rows.get(i);
	}
}

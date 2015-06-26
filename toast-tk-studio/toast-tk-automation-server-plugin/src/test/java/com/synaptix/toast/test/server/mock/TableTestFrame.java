package com.synaptix.toast.test.server.mock;

import javax.swing.JFrame;
import javax.swing.JTable;

public class TableTestFrame extends JFrame {

	private JTable table;
	
	public TableTestFrame(){
		Object rowData[][] = {  { "Row1-Column1", "Row1-Column2", "Row1-Column3"},
                				{ "Row2-Column1", "Row2-Column2", "Row2-Column3"} };
		Object columnNames[] = { "Column One", "Column Two", "Column Three"};
		table = new JTable(rowData, columnNames);
		getContentPane().add(table);
		pack();
		setVisible(true);
	}
}

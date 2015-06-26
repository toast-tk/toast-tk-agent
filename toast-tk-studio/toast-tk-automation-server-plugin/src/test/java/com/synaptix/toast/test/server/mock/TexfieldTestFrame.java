package com.synaptix.toast.test.server.mock;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class TexfieldTestFrame extends JFrame {

	private JTextField inputField;
	
	public TexfieldTestFrame(){
		inputField = new JTextField();
		getContentPane().add(inputField);
		pack();
		setVisible(true);
	}

	public void setTextValue(String value) {
		inputField.setText(value);
	}
	
	public void setTextFocus() {
		inputField.requestFocus();
	}
}

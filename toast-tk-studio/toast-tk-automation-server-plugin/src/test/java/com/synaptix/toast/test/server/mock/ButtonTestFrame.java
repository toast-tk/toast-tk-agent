/*******************************************************************************
 *******************************************************************************/
package com.synaptix.toast.test.server.mock;

import javax.swing.JButton;
import javax.swing.JFrame;

public class ButtonTestFrame extends JFrame {

	private JButton buttonField;

	public ButtonTestFrame() {
		buttonField = new JButton("CLICK ME");
		getContentPane().add(buttonField);
		pack();
		setVisible(true);
	}

	public void setFocus() {
		buttonField.requestFocus();
	}
}

package com.synaptix.toast.test.server.mock;

import javax.swing.JFrame;
import javax.swing.JRadioButton;

import com.synaptix.toast.plugin.swing.server.SwingInspectionRecorder;
import com.synaptix.toast.test.TestSuiteHelper;

public class JRadioButtonTestFrame extends JFrame {

	private static final long serialVersionUID = 596546026271112299L;

	private JRadioButton jRadioButton;

	public JRadioButtonTestFrame() throws Exception {
		this.jRadioButton = new JRadioButton("Check Me !");
		getContentPane().add(jRadioButton);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) throws Exception {
		new JRadioButtonTestFrame();
	}
}
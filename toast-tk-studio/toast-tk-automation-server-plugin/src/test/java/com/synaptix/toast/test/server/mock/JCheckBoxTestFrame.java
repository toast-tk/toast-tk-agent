package com.synaptix.toast.test.server.mock;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import com.synaptix.toast.plugin.swing.server.SwingInspectionRecorder;
import com.synaptix.toast.test.TestSuiteHelper;

public class JCheckBoxTestFrame extends JFrame {

	private static final long serialVersionUID = 596546026271112299L;

	private JCheckBox jCheckBox;

	private SwingInspectionRecorder recorder;

	public JCheckBoxTestFrame() throws Exception {
		recorder = TestSuiteHelper.getInjector().getInstance(SwingInspectionRecorder.class);
		this.jCheckBox = new JCheckBox("Check Me !");
		getContentPane().add(jCheckBox);
		pack();
		setVisible(true);
		recorder.startRecording();
	}
	
	public static void main(String[] args) throws Exception {
		new JCheckBoxTestFrame();
	}
}
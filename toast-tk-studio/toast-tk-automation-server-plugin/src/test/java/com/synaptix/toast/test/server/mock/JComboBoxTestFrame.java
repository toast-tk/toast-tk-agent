package com.synaptix.toast.test.server.mock;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.synaptix.toast.plugin.swing.server.SwingInspectionRecorder;
import com.synaptix.toast.test.TestSuiteHelper;

public class JComboBoxTestFrame extends JFrame {

	private static final long serialVersionUID = 596546026271112299L;

	private JComboBox<String> jList;

	private SwingInspectionRecorder recorder;

	public JComboBoxTestFrame() throws Exception {
		recorder = TestSuiteHelper.getInjector().getInstance(SwingInspectionRecorder.class);
		JTabbedPane tabbedPane = new JTabbedPane();
		final String[] datas = new String[]{
				"one", "two", "three", "four"
		};
		this.jList = new JComboBox<String>(datas);
		tabbedPane.addTab("Tab0", null, new JPanel().add(jList), "Does nothing");
		getContentPane().add(tabbedPane);
		pack();
		setVisible(true);
		recorder.startRecording();
	}
	
	public static void main(String[] args) throws Exception {
		new JComboBoxTestFrame();
	}
}
package com.synaptix.toast.test.server.mock;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import com.synaptix.toast.plugin.swing.server.SwingInspectionRecorder;
import com.synaptix.toast.test.TestSuiteHelper;

public class JPopupMenuTestFrame extends JFrame {

	private static final long serialVersionUID = 596546026271112299L;

	private JPopupMenu jPopMenu;

	private JMenuItem klienciMenuItem = new JMenuItem("Klienci");

	private SwingInspectionRecorder recorder;

	public JPopupMenuTestFrame() throws Exception {
		recorder = TestSuiteHelper.getInjector().getInstance(SwingInspectionRecorder.class);
		JTabbedPane tabbedPane = new JTabbedPane();
		this.jPopMenu = new JPopupMenu("My Menu");
		jPopMenu.add(klienciMenuItem);
		JPanel panel = new JPanel();
		panel.add(new JLabel("Right-click for popup menu."));
		panel.setComponentPopupMenu(jPopMenu);
		panel.setMinimumSize(new Dimension(200, 200));
		tabbedPane.add(panel);
		getContentPane().add(tabbedPane);
		pack();
		setVisible(true);
		panel.add(this.jPopMenu);
		recorder.startRecording();
	}
	
	public static void main(String[] args) throws Exception {
		new JPopupMenuTestFrame();
	}
}
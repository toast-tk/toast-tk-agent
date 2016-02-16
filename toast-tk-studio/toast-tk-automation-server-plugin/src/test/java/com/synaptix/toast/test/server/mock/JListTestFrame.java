/*******************************************************************************
 *******************************************************************************/
package com.synaptix.toast.test.server.mock;

import javax.swing.JFrame;
import javax.swing.JList;

import com.synaptix.toast.plugin.swing.server.SwingInspectionRecorder;
import com.synaptix.toast.test.TestSuiteHelper;

public class JListTestFrame extends JFrame {

	private static final long serialVersionUID = 596546026271112299L;

	private JList jList;

	private SwingInspectionRecorder recorder;
	

	public JListTestFrame() {
		final String[] datas = new String[]{
				"one", "two", "three", "four"
		};
		this.jList = new JList(datas);
		getContentPane().add(jList);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new JListTestFrame();
		
	}
}
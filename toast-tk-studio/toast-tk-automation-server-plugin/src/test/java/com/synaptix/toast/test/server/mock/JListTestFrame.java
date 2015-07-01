package com.synaptix.toast.test.server.mock;

import javax.swing.JFrame;
import javax.swing.JList;

public class JListTestFrame extends JFrame {

	private static final long serialVersionUID = 596546026271112299L;

	private JList jList;

	public JListTestFrame() {
		final String[] datas = new String[]{"one", "two", "three", "four"};
		this.jList = new JList(datas);
		getContentPane().add(jList);
		pack();
		setVisible(true);
	}
}
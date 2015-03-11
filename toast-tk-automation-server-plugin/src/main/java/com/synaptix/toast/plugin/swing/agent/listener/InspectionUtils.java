package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class InspectionUtils {

	
	public static boolean match(Pattern p, String line) {
		return p.matcher(line).matches();
	}
	
	public static  boolean isComboBoxType(String targetType) {
		return "JComboBox".equals(targetType) || "ComboBox.list".equals(targetType);
	}

	public static  boolean isMenuItemType(String targetType) {
		return "JMenuItem".equals(targetType);
	}

	public static  boolean isMenuType(String targetType) {
		return "JMenu".equals(targetType);
	}

	public static  boolean isTableType(String targetType) {
		return "JTable".equals(targetType) || "JSyTable".equals(targetType);
	}
	
	public static  boolean isPopupMenuType(String targetType) {
		return targetType.contains("JPopupMenu");
	}

	public static  boolean isButtonType(String targetType) {
		return "JButton".equals(targetType);
	}
	
	public static  boolean isCheckBoxType(String targetType) {
		return "JCheckBox".equals(targetType);
	}
	
	public static void main(String[] args) {
		final JFrame frame = new JFrame();

		frame.setSize(300, 300);
		frame.setLocation(200, 200);
		frame.setLayout(new BorderLayout());

		frame.getContentPane().add(new JLabel("Drag to move", JLabel.CENTER),
				BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		menuBar.add(menu);
		JMenuItem item = new JMenuItem("Exit");
		menu.add(item);
		frame.setJMenuBar(menuBar);
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
				System.out.println("event = " + event);
			}
		},AWTEvent.MOUSE_EVENT_MASK);

		frame.setLayout(new BorderLayout());
		frame.pack();
		frame.setVisible(true);
	}
}

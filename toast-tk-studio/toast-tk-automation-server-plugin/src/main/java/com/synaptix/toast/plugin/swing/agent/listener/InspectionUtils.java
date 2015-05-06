package com.synaptix.toast.plugin.swing.agent.listener;

import java.util.regex.Pattern;

public class InspectionUtils {

	
	public static boolean match(Pattern p, String line) {
		return p.matcher(line).matches();
	}
	
	public static boolean isComboBoxType(String targetType) {
		return "JComboBox".equals(targetType) || "ComboBox.list".equals(targetType);
	}

	public static boolean isMenuItemType(String targetType) {
		return "JMenuItem".equals(targetType);
	}

	public static boolean isMenuType(String targetType) {
		return "JMenu".equals(targetType);
	}

	public static boolean isTableType(String targetType) {
		return "JTable".equals(targetType) || "JSyTable".equals(targetType);
	}
	
	public static boolean isPopupMenuType(String targetType) {
		return targetType.contains("JPopupMenu");
	}

	public static boolean isJListType(final String targetType) {
		return targetType.contains("JList");
	}
	
	public static  boolean isButtonType(String targetType) {
		return "JButton".equals(targetType);
	}
	
	public static  boolean isCheckBoxType(String targetType) {
		return "JCheckBox".equals(targetType);
	}
	
}

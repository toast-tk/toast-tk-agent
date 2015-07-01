
package com.synaptix.toast.plugin.swing.agent.listener;

import java.util.regex.Pattern;

public class InspectionUtils {

	
	public static boolean match(Pattern p, String line) {
		return p.matcher(line).matches();
	}
	

	public static boolean isJListType(final String targetType) {
		return targetType.contains("JList");
	}
	
	
	
}

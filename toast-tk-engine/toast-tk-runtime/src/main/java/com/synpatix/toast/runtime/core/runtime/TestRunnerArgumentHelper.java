package com.synpatix.toast.runtime.core.runtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.adapter.ActionAdapterSentenceRef;
import com.synaptix.toast.core.runtime.IRepositorySetup;

public class TestRunnerArgumentHelper {

	public static Object buildActionAdapterArgument(IRepositorySetup repoSetup, String group) {
		group = group.replaceAll("\\*", "");
		if (isOutputVariable(group)) {
			return group.substring(1);
		} 
		else if (isInputVariable(group)) {
			Object object = repoSetup.getUserVariables().get(group);
			if (object != null && object instanceof String) {
				String value = (String) object;
				value = handleValueWithNestedVars(repoSetup, value);
				object = value;
			}
			return object;
		}
		return group;
	}

	private static String handleValueWithNestedVars(IRepositorySetup repoSetup, String value) {
		Pattern p = Pattern.compile(ActionAdapterSentenceRef.VAR_IN_REGEX, Pattern.MULTILINE);
		Matcher m = p.matcher(value);
		int pos = 0;
		while (m.find()) {
			String varName = m.group(pos + 1);
			if (repoSetup.getUserVariables().containsKey(varName)) {
				Object varValue = repoSetup.getUserVariables().get(varName);
				value = value.replaceFirst("\\"+varName+"\\b", (String) varValue);
			}
		}
		return value;
	}

	private static boolean isInputVariable(String group) {
		return group.startsWith("$") 
				&& !group.substring(1).contains("$") 
				&& !group.substring(1).contains(Property.DEFAULT_PARAM_SEPARATOR);
	}

	private static boolean isOutputVariable(String group) {
		return group.startsWith("$$");
	}
}

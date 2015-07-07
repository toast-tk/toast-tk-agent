package com.synaptix.toast.runtime.core.runtime;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.adapter.ActionAdapterSentenceRef;
import com.synaptix.toast.core.runtime.IRepositorySetup;
import com.synaptix.toast.runtime.core.runtime.ActionItem.ActionCategoryEnum;
import com.synaptix.toast.runtime.core.runtime.ActionItem.ActionTypeEnum;

public class ArgumentHelper {
	
	private static final String ACTION_ITEM_REGEX = "\\{\\{([\\w:]+)\\}\\}";
	private static final List<ActionItem> actionItems = ActionItemDescriptionCollector.initActionItems();
	
	public static String convertActionSentenceToRegex(
		String actionSentence) {
		String convertedSentence = actionSentence;
		Pattern regexPattern = Pattern.compile(ACTION_ITEM_REGEX);
		Matcher matcher = regexPattern.matcher(actionSentence);
		int index = 1;
		while(matcher.find()) {
			String group = matcher.group(index++);
			String[] groupArray = group.split(":");
			String regex = null;
			if(hasOnlyDeclaredActionItemCategory(groupArray)) {
				String category = groupArray[0];
				ActionCategoryEnum categoryEnum = ActionItem.ActionCategoryEnum.valueOf(category);
				regex = getActionItemRegex(categoryEnum, ActionItem.ActionTypeEnum.string);
			}
			else if(hasDeclaredCategoryAndType(groupArray)) {
				String category = groupArray[0];
				ActionCategoryEnum categoryEnum = ActionItem.ActionCategoryEnum.valueOf(category);
				String type = groupArray[1];
				ActionTypeEnum typeEnum = ActionItem.ActionTypeEnum.valueOf(type);
				regex = getActionItemRegex(categoryEnum, typeEnum);
			}
			else if(hadDeclaredAll(groupArray)) {
				String category = groupArray[1];
				ActionCategoryEnum categoryEnum = ActionItem.ActionCategoryEnum.valueOf(category);
				String type = groupArray[2];
				ActionTypeEnum typeEnum = ActionItem.ActionTypeEnum.valueOf(type);
				regex = getActionItemRegex(categoryEnum, typeEnum);
			}
			if(regex != null) {
				convertedSentence = matcher.replaceFirst(regex);
			}
		}
		return convertedSentence;
	}

	private static String getActionItemRegex(
		ActionCategoryEnum categoryEnum,
		ActionTypeEnum typeEnum) {
		for(ActionItem actionItem : actionItems) {
			if(actionItem.category.equals(categoryEnum)) {
				if(actionItem.kind.equals(typeEnum)) {
					return actionItem.regex;
				}
			}
		}
		return null;
	}
	
	private static boolean hadDeclaredAll(
		String[] groupArray) {
		return groupArray.length == 3;
	}

	private static boolean hasDeclaredCategoryAndType(
		String[] groupArray) {
		return groupArray.length == 2;
	}

	private static boolean hasOnlyDeclaredActionItemCategory(
		String[] groupArray) {
		return groupArray.length == 1;
	}
	
	public static Object buildActionAdapterArgument(
		IRepositorySetup repoSetup,
		String group) {
		group = group.replaceAll("\\*", "");
		if(isOutputVariable(group)) {
			return group.substring(1);
		}
		else if(isInputVariable(group)) {
			Object object = repoSetup.getUserVariables().get(group);
			if(object != null && object instanceof String) {
				String value = (String) object;
				value = handleValueWithNestedVars(repoSetup, value);
				object = value;
			}
			return object;
		}
		return group;
	}

	private static String handleValueWithNestedVars(
		IRepositorySetup repoSetup,
		String value) {
		String variableRegex = "(\\$\\w+)";
		Pattern p = Pattern.compile(variableRegex, Pattern.MULTILINE);
		Matcher m = p.matcher(value);
		int pos = 0;
		while(m.find()) {
			String varName = m.group(pos + 1);
			if(repoSetup.getUserVariables().containsKey(varName)) {
				Object varValue = repoSetup.getUserVariables().get(varName);
				value = value.replaceFirst("\\" + varName + "\\b", (String) varValue);
			}
		}
		return value;
	}

	private static boolean isInputVariable(
		String group) {
		return group.startsWith("$")
			&& !group.substring(1).contains("$")
			&& !group.substring(1).contains(Property.DEFAULT_PARAM_SEPARATOR);
	}

	private static boolean isOutputVariable(
		String group) {
		return group.startsWith("$$");
	}
}

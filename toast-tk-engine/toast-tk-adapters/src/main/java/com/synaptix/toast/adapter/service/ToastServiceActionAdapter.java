package com.synaptix.toast.adapter.service;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;

import com.synaptix.toast.adapter.utils.ActionAdapterHelper;
import com.synaptix.toast.core.adapter.ActionAdapterKind;
import com.synaptix.toast.core.annotation.Action;
import com.synaptix.toast.core.annotation.ActionAdapter;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.core.report.TestResult.ResultKind;

/**
 * Generic service action adapter
 * TODO: check if we need to move it in a synaptix plugin module 
 */
@ActionAdapter(value = ActionAdapterKind.service, name = "")
public class ToastServiceActionAdapter extends AbstractServiceActionAdapter {

	public ToastServiceActionAdapter() {
		super();
	}

	@Action( action = "\\$?(\\w+).([.|\\w|\\[|\\]|=]+) is ([.|\\w|\\[|\\]|=| |/|:]+)", description = "")
	public TestResult getOperationProperty(String componentName, String propertyName, String expected) {
		String value = getProperty(componentName, propertyName);
		if (value == null) {
			return new TestResult(propertyName + " is null", ResultKind.FAILURE);
		} else if (value.equals(expected)) {
			return new TestResult();
		} else {
			return new TestResult(propertyName + " is " + value + " (should be: " + expected + ")");
		}
	}

	@Action( action = "\\$?(\\w+).([.|\\w|\\[|\\]|=]+) is \\$([.|\\w|\\[|\\]|=| |/|:]+)", description = "")
	public TestResult getOperationPropertyWithVariable(String componentName, String propertyName, String userVariable) {
		String expected = (String) getUserVariables().get(userVariable);
		return getOperationProperty(componentName, propertyName, expected);
	}

	@Action( action = "Save \\$?(\\w+).([.|\\w|\\[|\\]|=]+) as \\$(\\w+)", description = "")
	public TestResult saveVariable(String componentName, String propertyName, String variableName) {
		String value = getProperty(componentName, propertyName);
		if (value == null) {
			return new TestResult(propertyName + " is null", ResultKind.ERROR);
		} else {
			getUserVariables().put(variableName, value);
			return new TestResult(value, ResultKind.INFO);
		}
	}

	@Action( action = "\\$?(\\w+).([.|\\w|\\[|\\]|=]+) null", description = "")
	public TestResult checkOperationPropertyNull(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("null")) {
			return new TestResult(propertyName + " is null", ResultKind.SUCCESS);
		} else {
			return new TestResult(propertyName + " is " + value);
		}
	}

	@Action( action = "\\$?(\\w+).([.|\\w|\\[|\\]|=]+) not null", description = "")
	public TestResult checkOperationPropertyNotNull(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("null")) {
			return new TestResult(propertyName + " is " + value);
		} else {
			return new TestResult(propertyName + " is not null", ResultKind.SUCCESS);
		}
	}

	@Action( action = "\\$?(\\w+).([.|\\w|\\[|\\]|=]+) empty", description = "")
	public TestResult checkOperationPropertyempty(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("[]")) {
			return new TestResult(propertyName + " is empty", ResultKind.SUCCESS);
		} else {
			return new TestResult(propertyName + " is " + value);
		}
	}

	@Action( action = "(\\w+).([.|\\w|\\[|\\]|=]+) not empty", description = "")
	public TestResult checkOperationPropertyNotEmpty(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("[]")) {
			return new TestResult(propertyName + " is " + value);
		} else {
			return new TestResult(propertyName + " is not empty", ResultKind.SUCCESS);
		}
	}

	@Action(action = "New test", description = "")
	public TestResult getOperationProperty() {
		getUserVariables().clear();
		return new TestResult();
	}

	@Action(action = "Show \\$?(\\w+)\\.([.|\\w]+)", description = "", display = true)
	public TestResult displayOperationProperty(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null) {
			return new TestResult(propertyName + " is null", ResultKind.ERROR);
		} else {
			TestResult testResult = new TestResult();
			testResult.setMessage(value);
			testResult.setResultKind(ResultKind.INFO);
			return testResult;
		}
	}

	@Action(action = "Show \\$(\\w+)", description = "", display = true)
	public TestResult displayOperationProperty(String variableName) {
		Object value = getUserVariables().get(variableName);
		if (value == null) {
			return new TestResult("$" + variableName + " is null", ResultKind.ERROR);
		} else {
			TestResult testResult = new TestResult();
			testResult.setMessage(value.toString());
			testResult.setResultKind(ResultKind.INFO);
			return testResult;
		}
	}

	@Action(action = "Select ([\\w| ]+) : (\\w+) as \\$?(\\w+)", description = "", display = true)
	public TestResult getComponent(String componentName, String idValue, String variableName) {
		Object component = findComponent(componentName, idValue, variableName);
		return component != null ? new TestResult() : new TestResult("Object not found");
	}

	@Action(action = "Select ([\\w| ]+) : \\$(\\w+) as \\$?(\\w+)", description = "", display = true)
	public TestResult getComponentFromVariable(String componentName, String idValue, String variableName) {
		Object component = findComponent(componentName, (String) getUserVariables().get(idValue), variableName);
		return component != null ? new TestResult() : new TestResult("Object not found");
	}

	@Action(action = "Select ([\\w| ]+) where (\\w+) = ([\\w|$]+) as \\$?(\\w+)", description = "", display = true)
	public TestResult selectWhere(String objectType, String propertyName, String value, String variable) {
		if (StringUtils.startsWith(value, "$")) {
			Object object = getUserVariables().get(value);
			if (object != null && object instanceof String) {
				value = (String) object;
			} else {
				return new TestResult("Could not parse value " + value, ResultKind.ERROR);
			}
		}
		Object component = findComponent(objectType, propertyName, value, variable);
		return component != null ? new TestResult() : new TestResult("Object not found");
	}

	@Action( action = "Set system date time ()", description = "")
	public TestResult setSystemDateTime(String dateTime) {
		LocalDateTime dt = ActionAdapterHelper.parseLocalDateTimeFromString(dateTime);
		DateTimeUtils.setCurrentMillisFixed(dt.toDateTime().getMillis());
		return new TestResult();
	}

	@Action( action = "Reset system date time", description = "")
	public TestResult customerOrderRouting(String dateTime) {
		DateTimeUtils.setCurrentMillisSystem();
		return new TestResult();
	}

	@Action( action = "\\$(\\w+) contains (\\w+)=([.|\\w|\\[|\\]|=| |/|:]+)", description = "")
	public TestResult checkList(String componentName, String propertyName, String expected) {
		Object component = getComponent(componentName);
		if (component instanceof Collection) {
			Collection<?> collection = (Collection<?>) component;
			for (Object object : collection) {
				Object value = getValue(object, propertyName);
				if (value == null) {
					continue;
				}
				if (value.toString().equals(expected)) {
					return new TestResult();
				}
			}
		}
		return new TestResult("Not found in list", ResultKind.FAILURE);
	}

	@Action( action = "\\$(\\w+) size is (\\d+)", description = "")
	public TestResult checkList(String componentName, String expectedSize) {
		Object component = getComponent(componentName);
		if (component instanceof Collection) {
			int size = ((Collection<?>) component).size();
			if (size == Integer.parseInt(expectedSize)) {
				return new TestResult();
			} else {
				return new TestResult("Size is " + size, ResultKind.FAILURE);
			}
		} else {
			return new TestResult("Variable $" + componentName + " not found", ResultKind.FAILURE);
		}
	}

}

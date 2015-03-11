package com.synpatix.toast.runtime.core.runtime;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;

import com.synaptix.toast.core.Check;
import com.synaptix.toast.core.Display;
import com.synaptix.toast.core.setup.TestResult;
import com.synaptix.toast.core.setup.TestResult.ResultKind;
import com.synpatix.toast.runtime.helper.FixtureHelper;

/**
 * Generic test methods (fixtures)
 *
 * @author Nicolas Sauvage
 */
public class RedPepperBackendFixture extends SynaptixBackendFixture {

	public RedPepperBackendFixture() {
		super();
	}

	@Check("\\$?(\\w+).([.|\\w|\\[|\\]|=]+) is ([.|\\w|\\[|\\]|=| |/|:]+)")
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

	@Check("\\$?(\\w+).([.|\\w|\\[|\\]|=]+) is \\$([.|\\w|\\[|\\]|=| |/|:]+)")
	public TestResult getOperationPropertyWithVariable(String componentName, String propertyName, String userVariable) {
		String expected = (String) getUserVariables().get(userVariable);
		return getOperationProperty(componentName, propertyName, expected);
	}

	@Check("Save \\$?(\\w+).([.|\\w|\\[|\\]|=]+) as \\$(\\w+)")
	public TestResult saveVariable(String componentName, String propertyName, String variableName) {
		String value = getProperty(componentName, propertyName);
		if (value == null) {
			return new TestResult(propertyName + " is null", ResultKind.ERROR);
		} else {
			getUserVariables().put(variableName, value);
			return new TestResult(value, ResultKind.INFO);
		}
	}

	@Check("\\$?(\\w+).([.|\\w|\\[|\\]|=]+) null")
	public TestResult checkOperationPropertyNull(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("null")) {
			return new TestResult(propertyName + " is null", ResultKind.SUCCESS);
		} else {
			return new TestResult(propertyName + " is " + value);
		}
	}

	@Check("\\$?(\\w+).([.|\\w|\\[|\\]|=]+) not null")
	public TestResult checkOperationPropertyNotNull(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("null")) {
			return new TestResult(propertyName + " is " + value);
		} else {
			return new TestResult(propertyName + " is not null", ResultKind.SUCCESS);
		}
	}

	@Check("\\$?(\\w+).([.|\\w|\\[|\\]|=]+) empty")
	public TestResult checkOperationPropertyempty(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("[]")) {
			return new TestResult(propertyName + " is empty", ResultKind.SUCCESS);
		} else {
			return new TestResult(propertyName + " is " + value);
		}
	}

	@Check("(\\w+).([.|\\w|\\[|\\]|=]+) not empty")
	public TestResult checkOperationPropertyNotEmpty(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("[]")) {
			return new TestResult(propertyName + " is " + value);
		} else {
			return new TestResult(propertyName + " is not empty", ResultKind.SUCCESS);
		}
	}

	@Check("New test")
	public TestResult getOperationProperty() {
		getUserVariables().clear();
		return new TestResult();
	}

	@Display("Show \\$?(\\w+)\\.([.|\\w]+)")
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

	@Display("Show \\$(\\w+)")
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

	@Display("Select ([\\w| ]+) : (\\w+) as \\$?(\\w+)")
	public TestResult getComponent(String componentName, String idValue, String variableName) {
		Object component = findComponent(componentName, idValue, variableName);
		return component != null ? new TestResult() : new TestResult("Object not found");
	}

	@Display("Select ([\\w| ]+) : \\$(\\w+) as \\$?(\\w+)")
	public TestResult getComponentFromVariable(String componentName, String idValue, String variableName) {
		Object component = findComponent(componentName, (String) getUserVariables().get(idValue), variableName);
		return component != null ? new TestResult() : new TestResult("Object not found");
	}

	@Display("Select ([\\w| ]+) where (\\w+) = ([\\w|$]+) as \\$?(\\w+)")
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

	@Check("Set system date time ()")
	public TestResult setSystemDateTime(String dateTime) {
		LocalDateTime dt = FixtureHelper.parseLocalDateTimeFromString(dateTime);
		DateTimeUtils.setCurrentMillisFixed(dt.toDateTime().getMillis());
		return new TestResult();
	}

	@Check("Reset system date time")
	public TestResult customerOrderRouting(String dateTime) {
		DateTimeUtils.setCurrentMillisSystem();
		return new TestResult();
	}

	@Check("\\$(\\w+) contains (\\w+)=([.|\\w|\\[|\\]|=| |/|:]+)")
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

	@Check("\\$(\\w+) size is (\\d+)")
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

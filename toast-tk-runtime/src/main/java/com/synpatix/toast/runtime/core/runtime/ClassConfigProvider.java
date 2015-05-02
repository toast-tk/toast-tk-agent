package com.synpatix.toast.runtime.core.runtime;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.synaptix.toast.adapter.utils.FixtureHelper;
import com.synpatix.toast.runtime.core.runtime.RepositorySetup.TestComponentConfig;
import com.synpatix.toast.runtime.core.runtime.RepositorySetup.TestEntityProperty;

@Singleton
public class ClassConfigProvider {

	private final Map<String, TestComponentConfig> classesConfig = new HashMap<String, TestComponentConfig>();

	public Map<String, TestComponentConfig> getConfigMap() {
		return classesConfig;
	}

	public String getTranslatedPropertyName(String testClassName, String testPropertyName) {
		testClassName = FixtureHelper.parseTestString(testClassName);
		testPropertyName = FixtureHelper.parseTestString(testPropertyName);

		TestEntityProperty testEntityProperty = classesConfig.get(testClassName).getFieldNameMap().get(testPropertyName);
		if (testEntityProperty != null) {
			return testEntityProperty.appName;
		} else {
			return testPropertyName;
		}
	}
}

package com.synpatix.toast.runtime;

import java.io.Serializable;
import java.util.Map;

import com.google.inject.Inject;
import com.synaptix.toast.core.runtime.ITestManager;
import com.synpatix.toast.runtime.core.runtime.ClassConfigProvider;
import com.synpatix.toast.runtime.core.runtime.RepositorySetup;


public abstract class AbstractTestManagerImpl implements ITestManager {

	@Inject
	private ClassConfigProvider classConfigProvider;

	@Inject
	private RepositorySetup autoSetup;

	@Override
	public void initDb() {
	}

	@Override
	public void closeDb() {
	}



	@Override
	public Object findObjectAndStorAsVarialble(String componentName, String idValue) {
		return null;
	}

	@Inject
	public void setAutoSetup(RepositorySetup autoSetup) {
		this.autoSetup = autoSetup;
	}

	@Override
	public int insertType(String tableName, Map<String, String> valueMap) throws Exception {
		return 0;
	}

	protected final int insertSql(String sql) throws Exception {
		int res = 0;
		return res;
	}

	@Override
	public Serializable addObject(Object entity) {
		return null;
	}

	@Override
	public int insertObject(Object entity) throws Exception {
		return 0;
	}

	@Override
	public String findStringValueForProperty(String property, String value, Object object) {
		return null;
	}

	@Override
	public void saveEntity(Object object) throws Exception {
	}

	
	@Override
	public void saveEntity(Class<?> entityClass, Map<String, Object> valueMap) throws Exception {
		
	}
	@Override
	public Object findObject(String entityName, String propertyName, String propertyValue) {
		return null;
	}
}

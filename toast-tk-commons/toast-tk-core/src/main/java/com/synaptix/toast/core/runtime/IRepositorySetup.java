package com.synaptix.toast.core.runtime;

import java.util.Collection;
import java.util.Map;

import com.synaptix.toast.core.report.TestResult;

public interface IRepositorySetup {

	public IFeedableSwingPage getSwingPage(
		String entityName);

	public Collection<IFeedableSwingPage> getSwingPages();

	public ITestManager getTestManager();

	public void addSwingPage(
		String fixtureName);

	public void addPage(
		String fixtureName);

	public TestResult addClass(
		String className,
		String testName,
		String searchBy);

	public TestResult addService(
		String testName,
		String className);

	public TestResult addDomain(
		String domainClassName,
		String domainTestName,
		String tableName);

	public TestResult addProperty(
		String componentName,
		String testName,
		String systemName,
		String componentAssociation);

	public TestResult insertComponent(
		String entityName2,
		Map<String, String> values2);

	public Class<?> getService(
		String fixtureName);

	public IFeedableWebPage getPage(
		String fixtureName);

	public void setUserVariables(
		Map<String, Object> userVariables);

	public Map<String, Object> getUserVariables();
}

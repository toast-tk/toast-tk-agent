package com.synaptix.toast.core.runtime;

import java.util.Collection;
import java.util.Map;

import com.synaptix.toast.core.report.TestResult;

public interface IActionItemRepository {

	public IFeedableSwingPage getSwingPage(
		String entityName);

	public Collection<IFeedableSwingPage> getSwingPages();

	public Collection<IFeedableWebPage> getWebPages();

	public void addSwingPage(
		String fixtureName);

	public void addPage(
		String fixtureName);

	public TestResult addClass(
		String className,
		String testName,
		String searchBy);


	public Class<?> getService(
		String fixtureName);

	public IFeedableWebPage getPage(
		String fixtureName);

	public void setUserVariables(
		Map<String, Object> userVariables);

	public Map<String, Object> getUserVariables();
}

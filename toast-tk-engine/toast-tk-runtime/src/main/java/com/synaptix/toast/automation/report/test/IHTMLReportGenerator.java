package com.synaptix.toast.automation.report.test;

import com.synaptix.toast.core.dao.ITestPage;

public interface IHTMLReportGenerator {

	public String generatePageHtml(
		ITestPage testPage);

	public void writeFile(
		String generatePageHtml,
		String pageName,
		String path);
}

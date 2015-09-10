package com.synaptix.toast.runtime.report;

import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.dao.domain.impl.report.Project;

public class HTMLReporter {

	public static final String getProjectHTMLReport(
		String name) {
		IProjectHtmlReportGenerator projectHtmlReportGenerator = new ThymeLeafProjectHTMLReporter();
		String projectReportHtml = projectHtmlReportGenerator.generateProjectReportHtml(name);
		return projectReportHtml;
	}
	
	public static final String getProjectHTMLReport(
		Project project) {
		IProjectHtmlReportGenerator projectHtmlReportGenerator = new ThymeLeafProjectHTMLReporter();
		String projectReportHtml = projectHtmlReportGenerator.generateProjectReportHtml(project);
		return projectReportHtml;
	}
	
	public static final String getTestPageHTMLReport(ITestPage test){
		ThymeLeafHTMLReporter reporter = new ThymeLeafHTMLReporter();
		String outputHtml = reporter.generatePageHtml(test);
		return outputHtml;
	}


}

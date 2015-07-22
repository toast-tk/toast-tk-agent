package com.synaptix.toast.automation.report;

import java.util.List;

import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.runtime.dao.DAOManager;

public class HTMLReporter {

	public static final String getProjectHTMLReport(
		String name) {
		Project project = DAOManager.getInstance().getLastProjectByName(name);
		List<Project> projectHistory = DAOManager.getInstance().getProjectHistory(project);
		IProjectHtmlReportGenerator projectHtmlReportGenerator = new ThymeLeafProjectHTMLReporter();
		String projectReportHtml = projectHtmlReportGenerator.generateProjectReportHtml(project, projectHistory);
		return projectReportHtml;
	}
	
	public static final String getProjectHTMLReport(
		Project project) {
		List<Project> projectHistory = DAOManager.getInstance().getProjectHistory(project);
		IProjectHtmlReportGenerator projectHtmlReportGenerator = new ThymeLeafProjectHTMLReporter();
		String projectReportHtml = projectHtmlReportGenerator.generateProjectReportHtml(project, projectHistory);
		return projectReportHtml;
	}
	
	public static final String getTestPageHTMLReport(ITestPage test){
		ThymeLeafHTMLReporter reporter = new ThymeLeafHTMLReporter();
		String outputHtml = reporter.generatePageHtml(test);
		return outputHtml;
	}


}

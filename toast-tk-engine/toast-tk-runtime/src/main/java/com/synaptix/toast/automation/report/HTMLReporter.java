package com.synaptix.toast.automation.report;

import java.util.List;

import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.runtime.dao.DAOManager;

public class HTMLReporter {

	public static final String getProjectHTMLReport(
		String name) {
		Project project = DAOManager.getInstance().getLastProjectByName(name);
		List<Project> projectHistory = DAOManager.getInstance().getProjectHistory(project);
		ProjectHtmlReportGenerator projectHtmlReportGenerator = new ProjectHtmlReportGenerator();
		String projectReportHtml = projectHtmlReportGenerator.generateProjectReportHtml(project, projectHistory);
		return projectReportHtml;
	}

	public static final String getProjectHTMLReport(
		Project project) {
		List<Project> projectHistory = DAOManager.getInstance().getProjectHistory(project);
		ProjectHtmlReportGenerator projectHtmlReportGenerator = new ProjectHtmlReportGenerator();
		String projectReportHtml = projectHtmlReportGenerator.generateProjectReportHtml(project, projectHistory);
		return projectReportHtml;
	}
}

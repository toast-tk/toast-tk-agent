package com.synaptix.toast.automation.report.project;

import java.util.List;

import com.synaptix.toast.dao.domain.impl.report.Project;


public interface IProjectHtmlReportGenerator {

	String generateProjectReportHtml(
		Project project,
		List<Project> projectHistory);

	String generateProjectReportHtml(
		Project project,
		List<Project> projectsHistory,
		String reportFolderPath);
}

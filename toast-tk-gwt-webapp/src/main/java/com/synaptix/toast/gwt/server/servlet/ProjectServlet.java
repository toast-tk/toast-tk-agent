package com.synaptix.toast.gwt.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.dao.report.ProjectHtmlReportGenerator;
import com.synaptix.toast.dao.service.dao.access.project.ProjectDaoService;

@Singleton
public class ProjectServlet extends HttpServlet {

	ProjectDaoService projectService;

	@Inject
	public ProjectServlet(ProjectDaoService.Factory pfactory) {
		super();
		projectService = pfactory.create("test_project_db");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String pName = req.getParameter("project");
			String iteration = req.getParameter("iteration");
			Project p = projectService.getByNameAndIteration(pName, iteration);
			List<Project> projectHistory = projectService.getProjectHistory(p);
			ProjectHtmlReportGenerator projectHtmlReportGenerator = new ProjectHtmlReportGenerator();
			String projectReportHtml = projectHtmlReportGenerator.generateProjectReportHtml(p, projectHistory, null);
			projectHtmlReportGenerator.writeFile(projectReportHtml, "Project Test", null);
			resp.setContentType("text/html");
			PrintWriter out = resp.getWriter();
			out.write(projectReportHtml);
			out.close();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}

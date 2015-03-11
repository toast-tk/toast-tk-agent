package com.synaptix.toast.gwt.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.synaptix.toast.dao.domain.impl.report.Campaign;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.report.ProjectHtmlReportGenerator;
import com.synaptix.toast.dao.service.dao.access.project.ProjectDaoService;

@Singleton
public class TestReportServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1497369902256863496L;
	ProjectDaoService projectService;

	@Inject
	public TestReportServlet(ProjectDaoService.Factory pfactory) {
		super();
		projectService = pfactory.create("test_project_db");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String pName = req.getParameter("project");
			String iter = req.getParameter("iteration");
			String tName = req.getParameter("test");
			Project p = projectService.getByNameAndIteration(pName, iter);
			TestPage testPage = null;
			for (Campaign c : p.getCampaigns()) {
				for (TestPage tp : c.getTestCases()) {
					if (tName.equals(tp.getName())) {
						testPage = tp;
						break;
					}
				}
				if (testPage != null) {
					break;
				}
			}
			String pageReport = ProjectHtmlReportGenerator.generatePageReport(null, testPage);
			resp.setContentType("text/html");
			PrintWriter out = resp.getWriter();
			out.write(pageReport);
			out.close();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}

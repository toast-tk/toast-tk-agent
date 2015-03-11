/**
 * 
 */
package com.synaptix.toast.dao.report;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import com.synaptix.toast.dao.domain.impl.report.Campaign;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.dao.domain.impl.test.TestPage;

/**
 * @author E413544
 * 
 */
public class ProjectHtmlReportGenerator {
	private static int HISTORY_SIZE = 20;

	public String generateProjectReportHtml(Project project, List<Project> projectsHistory) {
		return generateProjectReportHtml(project, projectsHistory, null);
	}

	public String generateProjectReportHtml(Project project, List<Project> projectsHistory, String reportFolderPath) {
		projectsHistory = projectsHistory.subList(projectsHistory.size() - Math.min(HISTORY_SIZE, projectsHistory.size()), projectsHistory.size());

		StringBuilder report = new StringBuilder();
		String execTrend = getExecTrendData(project, projectsHistory);
		String resultTrend = getResultTrendData(project, projectsHistory);

		report.append("<!DOCTYPE html>\r\n" + "<html>\r\n" + "	<head>\r\n" + "		<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\r\n"
				+ "		<meta http-equiv=\"cache-control\" content=\"no-cache\">\r\n" + "		<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9\">\r\n" + "		\r\n"
				+ "		<link type=\"text/css\" rel=\"stylesheet\" href=\"http://netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css\">\r\n"
				+ "		<script src=\"http://netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js\"></script>\r\n" + "		\r\n" + "		<title>Test Report</title>\r\n" + "		<style>\r\n"
				+ "			body {\r\n" + "				background: url(\"images/bg_general.jpg\") repeat-x scroll 0 0 #55A9ED;\r\n" + "				color: #4C4C4C;\r\n"
				+ "				font-family: \"Helvetica Neue\",Helvetica,Arial,sans-serif;\r\n" + "				font-size: 14px;\r\n" + "				line-height: 20px;\r\n" + "				margin: 0;\r\n" + "				padding-top: 0;\r\n"
				+ "			}\r\n" + "			.container {\r\n" + "				width: 1000px;\r\n" + "			}\r\n" + "			.portalBody{\r\n" + "				background: #FFFFFF;\r\n" + "				padding: 10px;\r\n"
				+ "				border: 4px solid #ccc;\r\n" + "			}\r\n" + "		</style>\r\n" + "		\r\n" + "		<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>\r\n"
				+ "		<script type=\"text/javascript\">\r\n" + "		  google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});\r\n" + "		  google.setOnLoadCallback(drawChart);\r\n"
				+ "		  function drawChart() {\r\n" + "			var data = google.visualization.arrayToDataTable([\r\n" + "			  ['Iteration', 'OK', 'KO', 'Not Completed', 'Not Run'],\r\n");
		report.append(resultTrend);
		report.append("]);\r\n" + "			var options = {title:'Test Execution Trend',vAxis:{format: '0'}};\r\n" + "\r\n"
				+ "			var chart = new google.visualization.LineChart(document.getElementById('chart_div'));\r\n" + "			chart.draw(data, options);\r\n" + "		  }\r\n" + "		  \r\n"
				+ "		  //performance chart\r\n" + "		  google.setOnLoadCallback(drawPerfChart);\r\n" + "		  function drawPerfChart() {\r\n"
				+ "			var data = google.visualization.arrayToDataTable([\r\n" + "			  ['Iteration', 'Total Execution Time (ms)'],\r\n");
		report.append(execTrend);
		report.append("]);\r\n" + "\r\n" + "			var options = {\r\n" + "			  title: 'Test Performance Trend'\r\n" + "			};\r\n" + "\r\n"
				+ "			var chart = new google.visualization.LineChart(document.getElementById('chart_perf_div'));\r\n" + "			chart.draw(data, options);\r\n" + "		  }\r\n" + "		</script>\r\n" + "\r\n"
				+ "	</head>\r\n" + "	<body>\r\n" + "		<div class=\"container\">\r\n" + "			<div class=\"row-fluid portalBody\">\r\n" + "				<div class=\"report-header\">\r\n" + "					<h1>");
		report.append(project.getName());
		report.append(" - Daily Test Report");
		report.append(" - Iteration: #");
		report.append(project.getIteration());
		report.append("</h1> \r\n" + "					<hr>\r\n" + "				</div>	\r\n" + "				<div class=\"report-milestones\">\r\n" + "					<h2> Milestones</h2>\r\n" + "					<hr>\r\n"
				+ "					<table class=\"table table-bordered\">\r\n" + "						<thead>\r\n" + "							<tr>\r\n" + "								<th>Kick-Off Date</th>\r\n" + "								<th>Demo Delivery Date</th>\r\n"
				+ "								<th>Production Date</th>\r\n" + "							</tr>\r\n" + "						</thead>\r\n" + "						<tbody>\r\n" + "						  <tr>\r\n" + "							<td>");
		if (project.getStartDate() != null) {
			report.append(project.getStartDate());
		}
		report.append("</td>\r\n<td>");
		if (project.getDemoDate() != null) {
			report.append(project.getDemoDate());
		}
		report.append("</td>\r\n<td>");
		if (project.getProdDate() != null) {
			report.append(project.getProdDate());
		}
		report.append("</td>\r\n</tr>\r\n</tbody>\r\n</table>\r\n</div>\r\n<div class=\"report-milestones\">\r\n<h2> Comments</h2>\r\n<hr>");
		report.append("comments : "); // TODO
		report.append("No Comment !");
		report.append("</div>\r\n" + "				<div class=\"report-trends\">\r\n" + "					<h2> Trends</h2>\r\n" + "					<hr>\r\n" + "					<h3> Execution Status </h3>\r\n"
				+ "					<div id=\"chart_div\">\r\n" + "					</div>\r\n" + "					<h3> Run Performance Status </h3>\r\n" + "					<div id=\"chart_perf_div\">\r\n" + "					</div>\r\n" + "				</div>	\r\n"
				+ "				<div class=\"report-campaigns\">\r\n" + "					<h2> Campaigns</h2>\r\n" + "					<hr>");
		for (Campaign campaign : project.getCampaigns()) {
			report.append("<div class=\"report-campaign\">\r\n" + "						<h3>");
			report.append(campaign.getName());
			report.append("</h3>\r\n" + "						<hr>\r\n" + "						<table class=\"table table-bordered\">\r\n" + "						<thead>\r\n" + "							<tr>\r\n" + "								<th>Use Case</th>\r\n"
					+ "								<th>Prev. Status</th>\r\n" + "								<th>Status</th>\r\n" + "								<th>Prev. Duration (s)</th>\r\n" + "								<th>Duration (s)</th>\r\n"
					+ "								<th>Details</th>\r\n" + "							</tr>\r\n" + "						</thead>\r\n" + "						<tbody>\r\n" + "						  <tr>");

			for (TestPage testPage : campaign.getTestCases()) {
				report.append("<td>");
				report.append(testPage.getName().replace(".txt", ""));
				if (testPage.isPreviousIsSuccess()) {
					report.append("<td>OK</td>\r\n");
				} else {
					report.append("<td>KO</td>\r\n");
				}
				if (testPage.isSuccess()) {
					report.append("<td class=\"success\">OK</td>\r\n");
				} else {
					report.append("<td class=\"danger\">KO</td>\r\n");
				}
				report.append("<td>");
				report.append(((double) testPage.getPreviousExecutionTime()) / 1000);
				report.append("</td>");
				report.append("<td>");
				report.append(((double) testPage.getExecutionTime()) / 1000);
				report.append("</td>\r\n<td>");

				// Generate HTML report
				if (reportFolderPath != null) {
					generatePageReport(reportFolderPath, testPage);
				}

				report.append("<a href=\"");
				if (reportFolderPath != null) {
					report.append(testPage.getName() + ".html");
				} else {
					report.append("/test?project=" + project.getName() + "&iteration=" + project.getIteration() + "&test=" + testPage.getName());
				}
				report.append("\">");
				report.append("details");
				report.append("</a></td>\r\n" + "						  </tr>");
			}
			report.append("\r\n" + "						</tbody>\r\n" + "					</table>\r\n" + "					</div>");
		}
		report.append("</div>\r\n" + "\r\n" + "			</div>\r\n" + "		</div>\r\n" + "		\r\n" + "	</body>\r\n" + "</html>");

		return report.toString();
	}

	public static String generatePageReport(String reportFolderPath, TestPage testPage) {
		HtmlReportGenerator htmlReportGenerator = new HtmlReportGenerator();
		String generatePageHtml = htmlReportGenerator.generatePageHtml(testPage);
		if (reportFolderPath != null) {
			htmlReportGenerator.writeFile(generatePageHtml, testPage.getName(), reportFolderPath);
		}
		return generatePageHtml;
	}

	public String getExecTrendData(Project project, List<Project> projectsHistory) {
		StringBuilder execTrend = new StringBuilder();
		for (Project p : projectsHistory) {
			long executionTotal = 0;
			for (Campaign campaign : p.getCampaigns()) {
				for (TestPage testPage : campaign.getTestCases()) {
					executionTotal += testPage.getExecutionTime();
				}
			}
			execTrend.append("['");
			execTrend.append(p.getIteration());
			execTrend.append("', ");
			execTrend.append(executionTotal / 1000);
			execTrend.append("],");
		}
		long executionTotal = 0;
		for (Campaign campaign : project.getCampaigns()) {
			for (TestPage testPage : campaign.getTestCases()) {
				executionTotal += testPage.getExecutionTime();
			}
		}
		execTrend.append("['");
		execTrend.append(project.getIteration());
		execTrend.append("', ");
		execTrend.append(executionTotal / 1000);
		execTrend.append("]");
		return execTrend.toString();
	}

	public String getResultTrendData(Project project, List<Project> projectsHistory) {
		StringBuilder execTrend = new StringBuilder();
		for (Project p : projectsHistory) {
			execTrend.append("['");
			execTrend.append(p.getIteration());
			execTrend.append("', ");
			execTrend.append(p.getTotalOk());
			execTrend.append(", ");
			execTrend.append(p.getTotalKo());
			execTrend.append(", 0,0");
			execTrend.append("],");
		}
		execTrend.append("['");
		execTrend.append(project.getIteration());
		execTrend.append("', ");
		execTrend.append(project.getTotalOk());
		execTrend.append(", ");
		execTrend.append(project.getTotalKo());
		execTrend.append(", 0,0");
		execTrend.append("]");
		return execTrend.toString();
	}

	/**
	 * @param reportFolderPath
	 * @param generatePageHtml
	 */
	public void writeFile(String report, String projectName, String reportFolderPath) {
		try {
			FileWriter fstream = new FileWriter(reportFolderPath + "index.html");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(report);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

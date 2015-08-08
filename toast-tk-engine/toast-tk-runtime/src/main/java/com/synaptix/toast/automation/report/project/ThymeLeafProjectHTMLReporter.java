package com.synaptix.toast.automation.report.project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.dao.service.dao.access.project.ProjectDaoService;

/**
 * http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html
 * 
 */
public class ThymeLeafProjectHTMLReporter implements IProjectHtmlReportGenerator {

	public void writeFile(
		String report,
		String pageName,
		String reportFolderPath) {
		try {
			FileWriter fstream = new FileWriter(reportFolderPath + "\\" + pageName + ".html");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(report);
			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String generateHtmlReport(
		Project project,
		List<Project> projectHistory) {
		TemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCharacterEncoding("UTF-8");
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		Locale locale = LocaleUtils.toLocale("fr");
		final Context ctx = new Context(locale);
		ctx.setVariable("project", project);
		ctx.setVariable("projectsHistory", projectHistory);
		String htmlOutput = templateEngine.process("project_report_template.html", ctx);
		return htmlOutput;
	}
	

	@Override
	public String generateProjectReportHtml(
		Project project,
		List<Project> projectHistory) {
		return generateHtmlReport(project, projectHistory);
	}

	@Override
	public String generateProjectReportHtml(
		Project project,
		List<Project> projectsHistory,
		String reportFolderPath) {
		ThymeLeafProjectHTMLReporter reporter = new ThymeLeafProjectHTMLReporter();
		String generateHtmlReport = reporter.generateHtmlReport(project, projectsHistory);
		reporter.writeFile(generateHtmlReport, project.getName(), reportFolderPath);
		return generateHtmlReport;
	}
	
	public static void main(
		String[] args) {
		Injector in = Guice.createInjector(new MongoModule("10.23.252.131", 27017));
		ProjectDaoService.Factory repoFactory = in.getInstance(ProjectDaoService.Factory.class);
		ProjectDaoService service = repoFactory.create("test_project_db");
		
		String name = "Prevision TNR CI";
		Project referenceProjectByName = service.getReferenceProjectByName(name);

		Project lastByName = service.getLastByName(name);
		List<Project> projectHistory = service.getProjectHistory(lastByName);
		
		ThymeLeafProjectHTMLReporter reporter = new ThymeLeafProjectHTMLReporter();
		String generateHtmlReport = reporter.generateHtmlReport(lastByName, projectHistory);
		reporter.writeFile(generateHtmlReport, "report", "C:\\tmp");
		
	}

}

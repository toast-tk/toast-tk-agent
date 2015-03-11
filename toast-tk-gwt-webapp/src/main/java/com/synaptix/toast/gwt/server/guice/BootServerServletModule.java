package com.synaptix.toast.gwt.server.guice;

import java.util.HashMap;
import java.util.Map;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.gwt.server.GreetingServiceImpl;
import com.synaptix.toast.gwt.server.servlet.LaunchExecServlet;
import com.synaptix.toast.gwt.server.servlet.ProjectServlet;
import com.synaptix.toast.gwt.server.servlet.TestReportServlet;

public class BootServerServletModule extends ServletModule {

	@Override
	protected void configureServlets() {
		install(new MongoModule());

		serve("/toast/greet").with(GreetingServiceImpl.class);
		serve("/reports").with(ProjectServlet.class);
		serve("/test").with(TestReportServlet.class);
		serve("/run").with(LaunchExecServlet.class);

		bind(HttpServletDispatcher.class).in(Singleton.class);
		Map<String, String> restParams = new HashMap<String, String>();
		restParams.put("resteasy.servlet.mapping.prefix", "/rest");
		serve("/rest/*").with(HttpServletDispatcher.class, restParams);

	}
}

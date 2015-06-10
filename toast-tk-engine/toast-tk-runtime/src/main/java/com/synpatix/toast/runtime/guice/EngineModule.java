package com.synpatix.toast.runtime.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synaptix.toast.adapter.service.ToastServiceActionAdapter;
import com.synaptix.toast.automation.report.IHTMLReportGenerator;
import com.synaptix.toast.automation.report.ThymeLeafHTMLReporter;
import com.synaptix.toast.core.runtime.IRepositorySetup;
import com.synpatix.toast.runtime.core.runtime.DefaultRepositoryTypeHandler;
import com.synpatix.toast.runtime.core.runtime.RepositorySetup;

public class EngineModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IRepositorySetup.class).to(RepositorySetup.class).in(Singleton.class);
		bind(IHTMLReportGenerator.class).to(ThymeLeafHTMLReporter.class);
		bind(ToastServiceActionAdapter.class).in(Singleton.class);
		install(new DefaultRepositoryTypeHandler());
	}

}

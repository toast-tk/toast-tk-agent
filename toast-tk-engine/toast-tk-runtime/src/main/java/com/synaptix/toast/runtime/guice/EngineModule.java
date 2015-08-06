package com.synaptix.toast.runtime.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synaptix.toast.automation.report.IHTMLReportGenerator;
import com.synaptix.toast.automation.report.ThymeLeafHTMLReporter;
import com.synaptix.toast.core.runtime.IActionItemRepository;
import com.synaptix.toast.runtime.core.runtime.ActionItemRepository;

public class EngineModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IActionItemRepository.class).to(ActionItemRepository.class).in(Singleton.class);
		bind(IHTMLReportGenerator.class).to(ThymeLeafHTMLReporter.class);
		install(new RunnerModule());
	}
}

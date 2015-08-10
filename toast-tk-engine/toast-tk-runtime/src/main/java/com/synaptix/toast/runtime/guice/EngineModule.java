package com.synaptix.toast.runtime.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synaptix.toast.core.runtime.IActionItemRepository;
import com.synaptix.toast.runtime.core.ActionItemRepository;
import com.synaptix.toast.runtime.report.test.IHTMLReportGenerator;
import com.synaptix.toast.runtime.report.test.ThymeLeafHTMLReporter;

public class EngineModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IActionItemRepository.class).to(ActionItemRepository.class).in(Singleton.class);
		bind(IHTMLReportGenerator.class).to(ThymeLeafHTMLReporter.class);
		install(new RunnerModule());
	}
}

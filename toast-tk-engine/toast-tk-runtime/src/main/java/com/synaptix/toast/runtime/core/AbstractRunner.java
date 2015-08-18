package com.synaptix.toast.runtime.core;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Injector;
import com.synaptix.toast.core.annotation.craft.FixMe;
import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.runtime.parse.TestParser;
import com.synaptix.toast.runtime.report.test.IHTMLReportGenerator;
import com.synaptix.toast.runtime.utils.RunUtils;

@FixMe(todo="make the runner generic")
public abstract class AbstractRunner {

	private static final Logger LOG = LogManager.getLogger(AbstractRunner.class);
	
	public abstract void tearDownEnvironment();

	public abstract void beginTest();

	public abstract void endTest();

	public abstract void initEnvironment();
}

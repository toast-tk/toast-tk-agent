
package com.synaptix.toast.runtime.core.runtime;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.synaptix.toast.adapter.ActionAdapterCollector;
import com.synaptix.toast.adapter.FixtureService;
import com.synaptix.toast.adapter.web.DefaultWebPage;
import com.synaptix.toast.automation.report.test.HTMLReportGenerator;
import com.synaptix.toast.automation.report.test.IHTMLReportGenerator;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.adapter.ActionAdapterKind;
import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.core.annotation.Action;
import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.core.report.TestResult.ResultKind;
import com.synaptix.toast.core.runtime.ErrorResultReceivedException;
import com.synaptix.toast.core.runtime.IFeedableSwingPage;
import com.synaptix.toast.core.runtime.IActionItemRepository;
import com.synaptix.toast.core.runtime.ITestManager;
import com.synaptix.toast.dao.domain.impl.test.SwingPageConfigLine;
import com.synaptix.toast.dao.domain.impl.test.TestLine;
import com.synaptix.toast.dao.domain.impl.test.WebPageConfigLine;
import com.synaptix.toast.dao.domain.impl.test.block.BlockLine;
import com.synaptix.toast.dao.domain.impl.test.block.SwingPageBlock;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import com.synaptix.toast.dao.domain.impl.test.block.VariableBlock;
import com.synaptix.toast.dao.domain.impl.test.block.WebPageBlock;
import com.synaptix.toast.runtime.core.IReportUpdateCallBack;
import com.synaptix.toast.runtime.core.runtime.block.BlockRunnerProvider;
import com.synaptix.toast.runtime.core.runtime.block.IBlockRunner;
import com.synaptix.toast.runtime.core.runtime.utils.ArgumentHelper;

public class TestRunner {

	private static final Logger LOG = LogManager.getLogger(TestRunner.class);
	private final IActionItemRepository objectRepository;
	private Injector injector;
	private IReportUpdateCallBack reportUpdateCallBack;
	private IHTMLReportGenerator htmlReportGenerator;
	private BlockRunnerProvider blockRunnerProvider;

	public TestRunner(
		IActionItemRepository repoSetup)
		throws IOException {
		this.objectRepository = repoSetup;
	}

	public static TestRunner FromInjector(
		Injector injector)
		throws IOException {
		TestRunner runner = new TestRunner(injector.getInstance(IActionItemRepository.class));
		runner.setInjector(injector);
		return runner;
	}

	public static TestRunner FromInjectorWithReportCallBack(
		Injector injector,
		IReportUpdateCallBack reportUpdateCallBack)
		throws IOException {
		TestRunner runner = new TestRunner(injector.getInstance(IActionItemRepository.class));
		runner.setInjector(injector);
		runner.setReportCallBack(reportUpdateCallBack);
		return runner;
	}

	private void setReportCallBack(
		IReportUpdateCallBack reportUpdateCallBack) {
		this.reportUpdateCallBack = reportUpdateCallBack;
	}

	private void setInjector(
		Injector injector) {
		this.injector = injector;
		this.blockRunnerProvider = injector.getInstance(BlockRunnerProvider.class);
		this.htmlReportGenerator = injector.getInstance(IHTMLReportGenerator.class);
	}

	/**
	 * Execute the different blocks within the test page
	 * 
	 * @param test page result
	 * @return
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException 
	 */
	public ITestPage run(
		ITestPage testPage,
		boolean inlineReport)
		throws IllegalAccessException, ClassNotFoundException {
		testPage.startExecution();
		initTestPageVariables(testPage);
		for(IBlock block : testPage.getBlocks()) {
			if(block instanceof ITestPage){
				run((ITestPage)block, inlineReport);
				handleInlineReport(testPage, inlineReport);
			}else{
				IBlockRunner blockRunner = blockRunnerProvider.getBlockRunner(block.getClass(), injector);
				if(blockRunner != null){
					blockRunner.run(testPage, block);
				}
			}
		}
		testPage.stopExecution();
		if(testPage.getParsingErrorMessage() != null) {
			LOG.info(testPage.getParsingErrorMessage());
		}
		return testPage;
	}

	private void handleInlineReport(ITestPage testPage, boolean inlineReport)
			throws IllegalAccessException {
		if (inlineReport) {
			String generatePageHtml = htmlReportGenerator.generatePageHtml(testPage);
			URL resource = this.getClass().getClassLoader() != null ? this
					.getClass().getClassLoader().getResource(Property.RESULT_FOLDER)
					: null;
			if (resource != null) {
				htmlReportGenerator.writeFile(generatePageHtml,
						testPage.getPageName(), resource.getPath());
			}
			if (reportUpdateCallBack != null) {
				reportUpdateCallBack.onUpdate(generatePageHtml);
			}
		}
	}

	private void initTestPageVariables(
		ITestPage testPage) {
		VariableBlock varBlock = (VariableBlock) testPage.getVarBlock();
		if(varBlock != null) {
			List<BlockLine> blockLines = varBlock.getBlockLines();
			for(BlockLine blockLine : blockLines) {
				String varName = blockLine.getCellAt(0);
				String varValue = blockLine.getCellAt(1);
				objectRepository.getUserVariables().put(varName, varValue);
			}
		}
	}

}
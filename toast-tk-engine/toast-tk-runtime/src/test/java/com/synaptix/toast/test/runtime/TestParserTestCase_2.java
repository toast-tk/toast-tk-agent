package com.synaptix.toast.test.runtime;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import com.synaptix.toast.runtime.parse.TestPageBlock;
import com.synaptix.toast.runtime.parse.TestParser;
import com.synaptix.toast.runtime.report.test.ThymeLeafHTMLReporter;

public class TestParserTestCase_2 {

	static StringBuilder b = new StringBuilder();

	@BeforeClass
	public static void init() {
		b.append("$var:=select 1 from dual").append("\n");
		b.append("|| scenario || swing ||").append("\n");
		b.append("| @swing Saisir *valeur* dans *ChooseApplicationRusDialog.applicationBox* |").append("\n");
		b.append("| @service Cliquer sur *ChooseApplicationRusDialog.OK* |").append("\n");
		b.append("| @service Cliquer sur *ChooseApplicationRusDialog.FIN* |").append("\n");
		b.append("| @toto Cliquer sur *ChooseApplicationRusDialog.FIN* |").append("\n");
		b.append("| Cliquer sur *ChooseApplicationRusDialog.KO* |").append("\n");
		b.append("| @swing:connector Saisir *valeur* dans *ChooseApplicationRusDialog.applicationBox* |").append("\n");
	}

	@Test
	public void testParserBlocks() {
		TestParser par = new TestParser();
		TestPage testPage = par.parseString(b.toString());
		Assert.assertNotNull(testPage.getVarBlock());
	}

	@Test
	public void testParserVarBlock() {
		TestParser parser = new TestParser();
		TestPageBlock varLine = parser.handleVarLine("$var:=select 1 from dual", null);
		Assert.assertNotNull(varLine);
	}

	@Test
	public void testParserVarBlockVarName() {
		TestParser parser = new TestParser();
		TestPageBlock varLine = parser.handleVarLine("$var := select 1 from dual", null);
		Assert.assertEquals(varLine.getLineAt(0).getCellAt(0), "$var");
	}

// @Test
// public void testParserMultiLineVarBlock() throws IOException {
// TestParser parser = new TestParser();
// StringBuilder varValueBuilder = new StringBuilder();
// varValueBuilder.append("Select * from Table").append("\n");
// varValueBuilder.append("Where col = 0").append("\n");
// StringBuilder builder = new StringBuilder();
// builder.append("$var := \"\"\"").append("\n");
// builder.append(varValueBuilder);
// builder.append("\"\"\"").append("\n");
// BufferedReader reader = new BufferedReader(new
// StringReader(builder.toString()));
// TestPageBlock varLine;
// varLine = parser.handleMultiVarLine(varValueBuilder.toString(), reader,
// null);
// Assert.assertEquals(varLine.getLineAt(0).getCellAt(1),varValueBuilder.toString());
// }
//
	@Test
	public void testParserVarBlockVarValue() {
		TestParser parser = new TestParser();
		String varValue = "select 1 from dual";
		TestPageBlock varLine = parser.handleVarLine("$var:=" + varValue, null);
		Assert.assertEquals(varLine.getLineAt(0).getCellAt(1), varValue);
	}

	// static check only
	private void testReportImageDisplay() {
		TestPage page = new TestPage();
		page.setName("test");
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture;
		try {
			capture = new java.awt.Robot().createScreenCapture(screenRect);
			TestResult result = new TestResult("failure", null);
			result.setContextualTestSentence("test");
			TestBlock block = new TestBlock();
			block.addLine("some test", "", "");
			block.getBlockLines().get(0).setTestResult(result);
			page.addBlock(block);
			ThymeLeafHTMLReporter reporter = new ThymeLeafHTMLReporter();
			String generatePageHtml = reporter.generatePageHtml(page);
			reporter.writeFile(generatePageHtml, "go", "c:\\temp");
		}
		catch(AWTException e) {
			e.printStackTrace();
		}
	}
}

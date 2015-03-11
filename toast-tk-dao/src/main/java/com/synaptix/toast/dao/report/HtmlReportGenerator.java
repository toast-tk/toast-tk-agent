package com.synaptix.toast.dao.report;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.core.setup.TestResult;
import com.synaptix.toast.core.setup.TestResult.ResultKind;
import com.synaptix.toast.dao.domain.impl.test.ComponentConfigLine;
import com.synaptix.toast.dao.domain.impl.test.TestLine;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.BlockLine;
import com.synaptix.toast.dao.domain.impl.test.block.CommentBlock;
import com.synaptix.toast.dao.domain.impl.test.block.ConfigBlock;
import com.synaptix.toast.dao.domain.impl.test.block.InsertBlock;
import com.synaptix.toast.dao.domain.impl.test.block.SetupBlock;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;

/**
 * @author E413544
 * 
 */
public class HtmlReportGenerator {
	public String generatePageHtml(ITestPage testPage) {
		StringBuilder report = new StringBuilder();
		report.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />");
		report.append("<LINK href=\"assets/style.css\" rel=\"stylesheet\" type=\"text/css\">");
		report.append("<div id=\"summary\">");
		report.append("Test " + testPage.getPageName() + "<br>");
		report.append("Effectué le " + testPage.getStartDateTime() + "<br>");
		report.append("Temps d'execution " + testPage.getExecutionTime() + "ms<br>");
		report.append("<br>");
		report.append("Errors: " + testPage.getTechnicalErrorNumber() + "<br>");
		report.append("Failures: " + testPage.getTestFailureNumber() + "<br>");
		report.append("Success: " + testPage.getTestSuccessNumber() + "<br>");
		report.append("</div>");
		report.append("<br>");

		for (IBlock block : testPage.getBlocks()) {
			if (block instanceof CommentBlock) {
				reportCommentBlock((CommentBlock) block, report);
			} else if (block instanceof TestBlock) {
				reportTestBlock((TestBlock) block, report);
			} else if (block instanceof TestPage) {
				reportTestPage((TestPage) block, report);
			} else if (block instanceof SetupBlock) {
				reportSetupBlock((SetupBlock) block, report);
			} else if (block instanceof ConfigBlock) {
				reportConfigBlock((ConfigBlock) block, report);
			} else if (block instanceof InsertBlock) {
				reportInsertBlock((InsertBlock) block, report);
			}
		}

		report.append("<script language=\"javascript\"> function toggle(text) { var ele = document.getElementById(text);if(ele.style.display == \"block\") { "
				+ "ele.style.display = \"none\";}else {ele.style.display = \"block\";}}</script>");

		return report.toString();
	}

	/**
	 * @param block
	 * @param report
	 */
	private void reportInsertBlock(InsertBlock block, StringBuilder report) {
		report.append("<table><thead>");
		// report.append(newRowWithResult(block));
		report.append("<th> Insert </th>");
		report.append("<th> " + block.getComponentName() + " </th></tr></thead>");
		// Print columns names
		report.append("<tr>");
		for (String cell : block.getColumns().getCells()) {
			report.append("<td>");
			report.append(cell);
			report.append("</td>");
		}
		report.append("</tr>");
		for (BlockLine line : block.getBlockLines()) {
			report.append(newRowWithResult(line.getTestResult()));
			for (String cell : line.getCells()) {
				report.append("<td>");
				report.append(cell);
				report.append("</td>");
			}
			addResultMessageCell(report, line.getTestResult());
			report.append("</tr>");
		}
		report.append("</table>");
	}

	/**
	 * @param block
	 * @param report
	 */
	private void reportConfigBlock(ConfigBlock block, StringBuilder report) {
		report.append("<table><tr>");
		report.append("<th> Configuration </th>");
		report.append("<th> " + block.getComponentName() + " </th></tr>");
		for (ComponentConfigLine line : block.getLines()) {
			TestResult testResult = line.getTestResult();
			report.append(newRowWithResult(testResult));
			report.append("<td>");
			report.append(line.getTestName());
			report.append("</td>");
			report.append("<td>");
			report.append(line.getSystemName());
			report.append("</td>");
			report.append("<td>");
			report.append(line.getComponentAssociation() != null ? line.getComponentAssociation() : "&nbsp");
			report.append("</td>");
			addResultMessageCell(report, testResult);
			report.append("</tr>");
		}
		report.append("</table>");
	}

	public void addResultMessageCell(StringBuilder report, TestResult testResult) {
		if (testResult != null && !testResult.isSuccess()) {
			report.append("<td class=\"message\">");
			report.append(testResult.getMessage());
			report.append("</td>");
		}
	}

	private String getResultKindAsString(ResultKind resultKind) {
		if (ResultKind.SUCCESS.equals(resultKind)) {
			return "resultSuccess";
		} else if (ResultKind.ERROR.equals(resultKind)) {
			return "resultError";
		} else if (ResultKind.FAILURE.equals(resultKind)) {
			return "resultFailure";
		} else if (ResultKind.INFO.equals(resultKind)) {
			return "resultInfo";
		}
		return "noResult";
	}

	/**
	 * @param block
	 * @param report
	 */
	private void reportSetupBlock(SetupBlock block, StringBuilder report) {
		report.append("<table>");
		report.append(newRowWithResult(block.getTestResult()));
		report.append("<th> Setup </th>");
		report.append("<th> " + block.getFixtureName() + " </th>");
		report.append("</tr>");

		// Print columns names
		report.append("<tr>");
		for (String cell : block.getColumns().getCells()) {
			report.append("<td>");
			report.append(cell);
			report.append("</td>");
		}
		report.append("</tr>");

		for (BlockLine line : block.getBlockLines()) {
			report.append(newRowWithResult(line.getTestResult()));
			for (String cell : line.getCells()) {
				report.append("<td>");
				report.append(cell);
				report.append("</td>");
			}
			addResultMessageCell(report, line.getTestResult());
			report.append("</tr>");
		}
		report.append("</table>");
	}

	public String newRowWithResult(TestResult testResult) {
		return "<tr class=\"" + getResultKindAsString(testResult) + "\">";
	}

	/**
	 * @param testResult
	 * @return
	 */
	private String getResultKindAsString(TestResult testResult) {
		if (testResult != null) {
			return getResultKindAsString(testResult.getResultKind());
		} else {
			return "noResult";
		}
	}

	public void reportTestPage(TestPage block, StringBuilder report) {
		report.append("<table><tr>");
		report.append("<th> Include </th>");
		report.append("<th> Success </th>");
		report.append("<th> Failure </th>");
		report.append("<th> Error </th>");
		report.append("</tr>");
		if (block.getTechnicalErrorNumber() > 0) {
			report.append("<tr class=\"resultError\">");
		} else if (block.getTestFailureNumber() > 0) {
			report.append("<tr class=\"resultFailure\">");
		} else if (block.getTestSuccessNumber() > 0) {
			report.append("<tr class=\"resultSuccess\">");
		} else {
			report.append("<tr class=\"resultInfo\">");
		}
		report.append("<td>");
		report.append("<button  onclick=\"javascript:toggle('" + block.getPageName() + "');\">" + block.getPageName() + "</button>");
		report.append("</td><td>");
		report.append(block.getTestSuccessNumber());
		report.append("</td><td>");
		report.append(block.getTestFailureNumber());
		report.append("</td><td>");
		report.append(block.getTechnicalErrorNumber());
		report.append("</td>");
		report.append("</tr></table> ");
		report.append("<div style=\"border-color: black;border-style: solid;display: none;\" id=\"" + block.getPageName() + "\">");
		report.append(this.generatePageHtml(block));
		report.append("</div>");
	}

	/**
	 * @param block
	 * @param report
	 */
	private void reportTestBlock(TestBlock block, StringBuilder report) {
		report.append("<div class=\"test\">");
		report.append("<table>");
		report.append("<thead>");
		// report.append(newRowWithResult(block.getTestResult()));
		report.append("<tr>");
		report.append("<th>");
		report.append("Test");
		report.append("</th><th>");
		report.append(block.getFixtureName());
		report.append("</th>");
		report.append("</tr>");
		report.append("</thead>");

		report.append("<tr>");
		report.append("<td>");
		report.append("Step");
		report.append("</td>");
		report.append("<td>");
		report.append("Expected");
		report.append("</td>");
		report.append("<td>");
		report.append("Result");
		report.append("</td>");
		report.append("<td>");
		report.append("Comment");
		report.append("</td>");
		report.append("<td>");
		report.append("Time");
		report.append("</td>");
		report.append("</tr>");

		for (TestLine line : block.getBlockLines()) {
			report.append(newRowWithResult(line.getTestResult()));
			report.append("<td>");
			report.append(line.getTest());
			report.append("</td>");
			report.append("<td>");
			report.append(line.getExpected() != null ? line.getExpected() : "&nbsp");
			report.append("</td>");
			report.append("<td>");
			report.append(line.getTestResult() != null ? formatStringToHtml(line.getTestResult().getMessage()) : "&nbsp");
			report.append("</td>");
			report.append("<td>");
			report.append(line.getTestCommentString() != null ? line.getTestCommentString() : "&nbsp");
			report.append("</td>");
			report.append("<td>");
			report.append(line.getExecutionTime() + ("ms"));
			report.append("</td>");
			report.append("</tr>");
		}
		report.append("</tr></table> ");
		report.append("</div>");
	}

	private String formatStringToHtml(String message) {
		return message != null ? message.replace("\n", "<br>") : "";
	}

	/**
	 * @param block
	 * @return
	 */
	private void reportCommentBlock(CommentBlock block, StringBuilder report) {
		report.append("<div class=\"comment\">");
		for (String line : block.getLines()) {
			if (line.startsWith("h3. ")) {
				report.append("<h3>");
				report.append(line.replace("h3. ", ""));
				report.append("</h3>");
			} else if (line.startsWith("h2. ")) {
				report.append("<h2>");
				report.append(line.replace("h2. ", ""));
				report.append("</h2>");
			} else if (line.startsWith("h1. ")) {
				report.append("<h1>");
				report.append(line.replace("h1. ", ""));
				report.append("</h1>");
			} else {
				report.append(line);
				report.append("<br>");
			}
		}
		report.append("</div>");
	}

	public void writeFile(String report, String pageName, String reportFolderPath) {
		try {
			FileWriter fstream = new FileWriter(reportFolderPath + "\\" + pageName + ".html");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(report);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

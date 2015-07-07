package com.synaptix.toast.automation.report;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.core.report.TestResult.ResultKind;
import com.synaptix.toast.dao.domain.impl.test.TestLine;

public class TemplateHelper {

	public static String getResultKindAsString(
		TestResult testResult) {
		if(testResult != null) {
			return getResultKindAsString(testResult.getResultKind());
		}
		else {
			return "";
		}
	}

	public static String getResultKindAsString(
		ResultKind resultKind) {
		if(ResultKind.SUCCESS.equals(resultKind)) {
			return "success";
		}
		else if(ResultKind.ERROR.equals(resultKind)) {
			return "warning";
		}
		else if(ResultKind.FAILURE.equals(resultKind)) {
			return "danger";
		}
		else if(ResultKind.INFO.equals(resultKind)) {
			return "info";
		}
		return "";
	}

	public static String getResultScreenshotAsBase64(
		TestResult testResult) {
		BufferedImage screenshot = testResult.getScreenShot();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(screenshot, "png", baos);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return Base64.encodeBase64String(baos.toByteArray());
	}

	public static String formatStringToHtml(
		TestLine line) {
		if(line.getTestResult() != null) {
			String message = line.getTestResult().getMessage();
			return message != null ? message.replace("\n", "<br>") : "";
		}
		return "&nbsp;";
	}

	public static String getStepSentence(
		TestLine line) {
		String contextualTestSentence = line.getTestResult() != null ? line.getTestResult().getContextualTestSentence() : null;
		return contextualTestSentence == null ? line.getTest() : contextualTestSentence;
	}

	public static boolean hasScreenShot(
		TestResult testResult) {
		return testResult != null && testResult.getScreenShot() != null;
	}
}

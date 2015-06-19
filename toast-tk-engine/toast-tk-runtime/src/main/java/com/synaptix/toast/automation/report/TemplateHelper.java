/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 10 juin 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.automation.report;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.apache.commons.codec.binary.Base64;

import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.core.report.TestResult.ResultKind;
import com.synaptix.toast.dao.domain.impl.test.TestLine;

public class TemplateHelper {
	
	public static String getResultKindAsString(TestResult testResult) {
		if (testResult != null) {
			return getResultKindAsString(testResult.getResultKind());
		} else {
			return "";
		}
	}
	
	public static String getResultScreenshotAsBase64(TestResult testResult){
		BufferedImage screenshot = testResult.getScreenShot();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(screenshot, "png", baos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Base64.encodeBase64String(baos.toByteArray());
	}

	public static String getResultKindAsString(ResultKind resultKind) {
		if (ResultKind.SUCCESS.equals(resultKind)) {
			return "success";
		} else if (ResultKind.ERROR.equals(resultKind)) {
			return "warning";
		} else if (ResultKind.FAILURE.equals(resultKind)) {
			return "danger";
		} else if (ResultKind.INFO.equals(resultKind)) {
			return "info";
		}
		return "";
	}

	
	public static String formatStringToHtml(TestLine line) {
		if(line.getTestResult() != null){
			String message = line.getTestResult().getMessage();
			return message != null ? message.replace("\n", "<br>") : "";
		}
		return "&nbsp;";
	}
	
	public static String getStepSentence(TestLine line){
		String contextualTestSentence = line.getTestResult() != null ? line.getTestResult().getContextualTestSentence() : null;
		return contextualTestSentence == null ? line.getTest() : contextualTestSentence;
	}
}

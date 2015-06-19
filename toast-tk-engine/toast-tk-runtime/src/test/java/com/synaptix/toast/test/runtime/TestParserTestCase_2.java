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

Creation date: 26 mars 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.test.runtime;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.automation.report.ThymeLeafHTMLReporter;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import com.synpatix.toast.runtime.core.parse.TestPageBlock;
import com.synpatix.toast.runtime.core.parse.TestParser;

public class TestParserTestCase_2 {
	
	static StringBuilder b = new StringBuilder();
	
	@BeforeClass
	public static void init(){
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
		Assert.assertEquals(varLine.getLineAt(0).getCellAt(0),"$var");
	}
	
//	@Test
//	public void testParserMultiLineVarBlock() throws IOException {
//		TestParser parser = new TestParser();
//		StringBuilder varValueBuilder = new StringBuilder();
//		varValueBuilder.append("Select * from Table").append("\n");
//		varValueBuilder.append("Where col = 0").append("\n");
//		StringBuilder builder = new StringBuilder();
//		builder.append("$var := \"\"\"").append("\n");
//		builder.append(varValueBuilder);
//		builder.append("\"\"\"").append("\n");
//		BufferedReader reader = new BufferedReader(new StringReader(builder.toString()));
//		TestPageBlock varLine;
//		varLine = parser.handleMultiVarLine(varValueBuilder.toString(), reader, null);
//		Assert.assertEquals(varLine.getLineAt(0).getCellAt(1),varValueBuilder.toString());
//	}
//	
	
	@Test
	public void testParserVarBlockVarValue() {
		TestParser parser = new TestParser();
		String varValue = "select 1 from dual";
		TestPageBlock varLine = parser.handleVarLine("$var:=" + varValue, null);
		Assert.assertEquals(varLine.getLineAt(0).getCellAt(1),varValue);
	}
	
	//static check only
	private void testReportImageDisplay(){
		TestPage page = new TestPage();
		page.setName("test");
		
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture;
		try {
			capture = new java.awt.Robot().createScreenCapture(screenRect);
			TestResult result = new TestResult("failure", capture);
			result.setContextualTestSentence("test");
			
			TestBlock block = new TestBlock();
			block.addLine("some test", "", "");
			block.getBlockLines().get(0).setTestResult(result);
			
			page.addBlock(block);
			
			ThymeLeafHTMLReporter reporter = new ThymeLeafHTMLReporter();
			String generatePageHtml = reporter.generatePageHtml(page);
			reporter.writeFile(generatePageHtml, "go", "c:\\temp");
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.core.adapter.ActionAdapterKind;
import com.synaptix.toast.dao.domain.impl.test.TestLine;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import com.synpatix.toast.runtime.core.parse.TestParser;
import com.synpatix.toast.runtime.core.runtime.TestLineDescriptor;

public class TestParserTestCase_1 {
	static StringBuilder b = new StringBuilder();
	
	@BeforeClass
	public static void init(){
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
		assertEquals(1, testPage.getBlocks().size());
	}
	
	@Test
	public void testParserBlockType() {
		TestParser par = new TestParser();
		TestPage testPage = par.parseString(b.toString());
		assertEquals(true, testPage.getBlocks().get(0) instanceof TestBlock);
	}
	
	@Test
	public void testParserBlockServiceNameParsing() {
		TestParser par = new TestParser();
		TestPage testPage = par.parseString(b.toString());
		TestBlock testBlock = (TestBlock)testPage.getBlocks().get(0);
		assertEquals("swing", testBlock.getFixtureName());
	}
	
	@Test
	public void testDefaultParserLineFixtureKind() {
		TestParser par = new TestParser();
		TestPage testPage = par.parseString(b.toString());
		TestBlock testBlock = (TestBlock)testPage.getBlocks().get(0);
		List<TestLine> blockLines = testBlock.getBlockLines();
		TestLine testLine = blockLines.get(4);
		assertEquals("Cliquer sur *ChooseApplicationRusDialog.KO*", testLine.getTest());
		TestLineDescriptor descriptor = new TestLineDescriptor(testBlock, testLine);
		assertEquals(ActionAdapterKind.swing, descriptor.getTestLineFixtureKind());
	}
	
	@Test
	public void testServiceParserLineFixtureKind() {
		TestParser par = new TestParser();
		TestPage testPage = par.parseString(b.toString());
		TestBlock testBlock = (TestBlock)testPage.getBlocks().get(0);
		List<TestLine> blockLines = testBlock.getBlockLines();
		TestLine testLine = blockLines.get(1);
		assertEquals("@service Cliquer sur *ChooseApplicationRusDialog.OK*", testLine.getTest());
		TestLineDescriptor descriptor = new TestLineDescriptor(testBlock, testLine);
		assertEquals(ActionAdapterKind.service, descriptor.getTestLineFixtureKind());
	}
	
	@Test
	public void testSwingParserLineFixtureKind() {
		TestParser par = new TestParser();
		TestPage testPage = par.parseString(b.toString());
		TestBlock testBlock = (TestBlock)testPage.getBlocks().get(0);
		List<TestLine> blockLines = testBlock.getBlockLines();
		assertEquals("@swing Saisir *valeur* dans *ChooseApplicationRusDialog.applicationBox*", blockLines.get(0).getTest());
		TestLineDescriptor descriptor = new TestLineDescriptor(testBlock, blockLines.get(0));
		assertEquals("", descriptor.getTestLineFixtureName());
	}
	
	
	@Test
	public void testSwingParserLineFixtureName() {
		TestParser par = new TestParser();
		TestPage testPage = par.parseString(b.toString());
		TestBlock testBlock = (TestBlock)testPage.getBlocks().get(0);
		List<TestLine> blockLines = testBlock.getBlockLines();
		assertEquals("@swing:connector Saisir *valeur* dans *ChooseApplicationRusDialog.applicationBox*", blockLines.get(5).getTest());
		TestLineDescriptor descriptor = new TestLineDescriptor(testBlock, blockLines.get(5));
		assertEquals(ActionAdapterKind.swing, descriptor.getTestLineFixtureKind());
		assertEquals("connector", descriptor.getTestLineFixtureName());
	}
	
	@AfterClass
	public static void end(){
	}
}

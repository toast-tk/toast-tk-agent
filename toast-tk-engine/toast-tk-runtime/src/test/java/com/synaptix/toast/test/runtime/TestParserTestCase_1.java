package com.synaptix.toast.test.runtime;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.core.adapter.ActionAdapterKind;
import com.synaptix.toast.dao.domain.impl.test.TestLine;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import com.synaptix.toast.runtime.bean.TestLineDescriptor;
import com.synaptix.toast.runtime.parse.TestParser;

public class TestParserTestCase_1 {

	static StringBuilder b = new StringBuilder();

	@BeforeClass
	public static void init() {
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
		TestBlock testBlock = (TestBlock) testPage.getBlocks().get(0);
		assertEquals("swing", testBlock.getFixtureName());
	}

	@Test
	public void testDefaultParserLineFixtureKind() {
		TestParser par = new TestParser();
		TestPage testPage = par.parseString(b.toString());
		TestBlock testBlock = (TestBlock) testPage.getBlocks().get(0);
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
		TestBlock testBlock = (TestBlock) testPage.getBlocks().get(0);
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
		TestBlock testBlock = (TestBlock) testPage.getBlocks().get(0);
		List<TestLine> blockLines = testBlock.getBlockLines();
		assertEquals("@swing Saisir *valeur* dans *ChooseApplicationRusDialog.applicationBox*", blockLines.get(0)
			.getTest());
		TestLineDescriptor descriptor = new TestLineDescriptor(testBlock, blockLines.get(0));
		assertEquals("", descriptor.getTestLineFixtureName());
	}

	@Test
	public void testSwingParserLineFixtureName() {
		TestParser par = new TestParser();
		TestPage testPage = par.parseString(b.toString());
		TestBlock testBlock = (TestBlock) testPage.getBlocks().get(0);
		List<TestLine> blockLines = testBlock.getBlockLines();
		assertEquals("@swing:connector Saisir *valeur* dans *ChooseApplicationRusDialog.applicationBox*", blockLines
			.get(5).getTest());
		TestLineDescriptor descriptor = new TestLineDescriptor(testBlock, blockLines.get(5));
		assertEquals(ActionAdapterKind.swing, descriptor.getTestLineFixtureKind());
		assertEquals("connector", descriptor.getTestLineFixtureName());
	}

	@AfterClass
	public static void end() {
	}
}

package com.synaptix.toast.test.runtime;

import com.synaptix.toast.automation.report.ThymeLeafHTMLReporter;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import com.synaptix.toast.runtime.core.parse.TestPageBlock;
import com.synaptix.toast.runtime.core.parse.TestParser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TestParserTestCase_3 {

	static StringBuilder b = new StringBuilder();

	@BeforeClass
	public static void init() {
		b.append(
//				"#include toto.txt\n" +
//				"#include titi.txt\n" +
//				"\n" +
//				"$valeur:=variable\n" +
//				"\n" +
				"Ceci est une ligne de commentaire.\n" +
				"Ceci est aussi une ligne de commentaire.\n" +
				"\n" +
				"|| scenario || swing ||\n" +
				"| @swing Saisir *$valeur* dans *ChooseApplicationRusDialog.applicationBox* |\n" +
				"| @service Cliquer sur *ChooseApplicationRusDialog.OK* |\n" +
				"| @service Cliquer sur *ChooseApplicationRusDialog.FIN* |\n" +
				"| @toto Cliquer sur *ChooseApplicationRusDialog.FIN* |\n" +
				"| Cliquer sur *ChooseApplicationRusDialog.KO* |\n" +
				"| @swing:connector Saisir *valeur* dans *ChooseApplicationRusDialog.applicationBox* |\n" +
				"\n" +
				"Encore des commentaires\n" +
				"Toujours des commentaires\n" +
				"\n" +
				"|| scenario || service ||\n" +
				"| @swing Saisir *valeur* dans *ChooseApplicationRusDialog.applicationBox* |\n" +
				"| @service Cliquer sur *ChooseApplicationRusDialog.OK* |\n" +
				"| @service Cliquer sur *ChooseApplicationRusDialog.FIN* |\n" +
				"| @toto Cliquer sur *ChooseApplicationRusDialog.FIN* |\n" +
				"| Cliquer sur *ChooseApplicationRusDialog.KO* |\n" +
				"| @swing:connector Saisir *valeur* dans *ChooseApplicationRusDialog.applicationBox* |").append("\n");
	}

	@Test
	public void testParserBlocks() {
		TestParser par = new TestParser();
		TestPage testPage = par.parseString(b.toString());
		Assert.assertNotNull(testPage.getVarBlock());
		Assert.assertNotNull(testPage.getBlocks().get(0));
		Assert.assertEquals("variable",testPage.getBlocks().get(0).getBlockType());
		Assert.assertNotNull(testPage.getBlocks().get(1));
		Assert.assertEquals("comment",testPage.getBlocks().get(1).getBlockType());
	}

}

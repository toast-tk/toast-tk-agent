package com.synaptix.toast.test.runtime;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.CommentBlock;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import com.synaptix.toast.dao.domain.impl.test.block.VariableBlock;
import com.synaptix.toast.runtime.parse.TestParser;

public class TestParserTestCase_3 {

    static StringBuilder b = new StringBuilder();

    @BeforeClass
    public static void init() {
        b.append(
                "$valeur:=variable\n" +
                        "$valeur2:=\"\"\"\n" +
                        "SELECT * T_TABLE\n" +
                        "WHERE VALUE = \'42\'\n" +
                        "\"\"\"\n" +
                        "\n" +
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
                        "$valeur2:=\"\"\"\n" +
                        "SELECT * T_TABLE\n" +
                        "WHERE VALUE = \'42\'\n" +
                        "\"\"\"\n" +
                        "\n" +
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
        TestPage testPage = par.readString(b.toString());
        Assert.assertNotNull(testPage);
        Assert.assertNotNull(testPage.getBlocks());
        int i = 0;
        IBlock block = testPage.getBlocks().get(i);
        Assert.assertEquals("variable", block.getBlockType());
        VariableBlock variableBlock = (VariableBlock) block;
        Assert.assertEquals(2, variableBlock.getBlockLines().size());
        i++;
        Assert.assertNotNull(testPage.getBlocks().get(i));
        Assert.assertEquals("comment", testPage.getBlocks().get(i).getBlockType());
        String commentLine = ((CommentBlock) testPage.getBlocks().get(i)).getLines().get(0);
        Assert.assertTrue(commentLine.startsWith("Ceci est une ligne de commentaire."));
        i++;
        Assert.assertEquals("test", testPage.getBlocks().get(i).getBlockType());
        i++;
        Assert.assertEquals("comment", testPage.getBlocks().get(i).getBlockType());
        i++;
        Assert.assertEquals("variable", testPage.getBlocks().get(i).getBlockType());
        i++;
        Assert.assertEquals("comment", testPage.getBlocks().get(i).getBlockType());
        commentLine = ((CommentBlock) testPage.getBlocks().get(i)).getLines().get(0);
        Assert.assertTrue(commentLine.startsWith("Toujours des commentaires"));
        i++;
        Assert.assertEquals("test", testPage.getBlocks().get(i).getBlockType());
        TestBlock testBlock = (TestBlock) testPage.getBlocks().get(i);
        Assert.assertEquals("Last scenario has 6 test lines", 6, testBlock.getBlockLines().size());
    }

}

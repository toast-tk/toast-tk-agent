package com.synaptix.toast.test.runtime;

import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.runtime.parse.TestParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 * Test swing page files
 */
public class TestParserTestCase_5 {

    @Test
    public void test() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL testFileUrl = classLoader.getResource("test_file_3.txt");
        Assert.assertNotNull(testFileUrl);
        String path = testFileUrl.getPath().substring(1);
        System.out.println("path = " + path);
        TestPage testPage = null;
        try {
            testPage = new TestParser().parse(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("testPage = " + testPage);

        Assert.assertNotNull(testPage);
        Assert.assertEquals(testPage.getBlocks().size(), 5); 
        //Error: the right value should be 1
        //currently creates a comment block and a swing page block
        //expecting only a swing page block

    }

}

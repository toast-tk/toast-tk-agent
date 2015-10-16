package com.synaptix.toast.test.runtime;

import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.runtime.parse.TestParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 * Test including files
 */
public class TestParserTestCase_4 {

    @Test
    public void test() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL testFileUrl = classLoader.getResource("test_file_1.txt");
        Assert.assertNotNull(testFileUrl);
        String path = testFileUrl.getPath();
        System.out.println("path = " + path);
        TestPage testPage = null;
        try {
            testPage = new TestParser().parse(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("testPage = " + testPage);

        Assert.assertNotNull(testPage);
        Assert.assertEquals("test_file_1.txt", testPage.getPageName());

    }

}

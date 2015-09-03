package com.synaptix.toast.test.runtime;

import java.io.IOException;

import org.junit.Test;

import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.runtime.parse.TestParser;

/**
 * Test including files
 */
public class TestParserTestCase_4 {

    @Test
    public void test() {
        ClassLoader classLoader = getClass().getClassLoader();
        String path = classLoader.getResource("test_file_1.txt").getPath().substring(1);
        System.out.println("path = " + path);
        TestPage parse = null;
        try {
            parse = new TestParser().parse(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("parse = " + parse);
    }

}

package com.synaptix.toast.test.runtime;

import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.runtime.parse.TestParser;
import org.junit.Test;

/**
 * Test including files
 */
public class TestParserTestCase_4 {

    @Test
    public void test() {
        ClassLoader classLoader = getClass().getClassLoader();
        String path = classLoader.getResource("test_file_1.txt").getPath().substring(1);
        System.out.println("path = " + path);
        TestPage parse = new TestParser().parse(path);
        System.out.println("parse = " + parse);
    }

}

package com.synaptix.toast.plugin.synaptix.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({PluginTestCenterCells.class})
public class PluginAllTests {

	@BeforeClass
	public static void setUp() {
		//PluginTestSuiteHelper.initInjector();
	}

	@AfterClass
	public static void tearDown() {

	}
}
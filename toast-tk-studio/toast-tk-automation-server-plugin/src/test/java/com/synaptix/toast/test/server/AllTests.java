package com.synaptix.toast.test.server;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.synaptix.toast.plugin.swing.server.SwingInspectionServer;
import com.synaptix.toast.plugin.swing.server.boot.Boot;
import com.synaptix.toast.test.TestSuiteHelper;

@RunWith(Suite.class)
@SuiteClasses({
		TestRequestHandlersForJTable.class, TestRequestHandlersForTextField.class
})
public class AllTests {

	@BeforeClass
	public static void setUp() {
		Boot b = new Boot();
		b.boot();
		TestSuiteHelper.initInjector(b.getModules());
	}

	@AfterClass
	public static void tearDown() {
		SwingInspectionServer instance = TestSuiteHelper.getInjector().getInstance(SwingInspectionServer.class);
		instance.close();
	}
}

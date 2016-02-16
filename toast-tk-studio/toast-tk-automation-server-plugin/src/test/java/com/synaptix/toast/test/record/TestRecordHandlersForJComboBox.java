package com.synaptix.toast.test.record;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.net.request.InitInspectionRequest;
import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.test.TestSuiteHelper;
import com.synaptix.toast.test.server.mock.FakeConnection;
import com.synaptix.toast.test.server.mock.JComboBoxTestFrame;

public class TestRecordHandlersForJComboBox {

	static String JLIST_TABLE_FIELD_NAME = "jlist";

	static JComboBoxTestFrame jComboBoxFrame;

	@BeforeClass
	public static void init() throws Exception {
		jComboBoxFrame = new JComboBoxTestFrame();
	}

	@Before
	public void initGuiRepository() {
		final InitRequestListener initRequestHandler = TestSuiteHelper.getInjector().getInstance(
			InitRequestListener.class);
		initRequestHandler.received(new FakeConnection(), new InitInspectionRequest());
	}

	@Test
	public void testGettingInputValue() {
		String idRequest = "fake-id";
		String expectedIndice = "2";
		String locator = JComboBoxTestFrame.class.getName() + ":" + JLIST_TABLE_FIELD_NAME;
		
	}

	public static CommandRequest buildJListCommandRequest() {
		return null;// for now
	}

	@AfterClass
	public static void end() {
		jComboBoxFrame.dispose();
	}
}

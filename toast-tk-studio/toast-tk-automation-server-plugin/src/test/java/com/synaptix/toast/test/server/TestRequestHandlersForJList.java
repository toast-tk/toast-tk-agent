package com.synaptix.toast.test.server;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.SwingActionRequestListener;
import com.synaptix.toast.test.TestSuiteHelper;
import com.synaptix.toast.test.server.mock.FakeConnection;
import com.synaptix.toast.test.server.mock.JListTestFrame;

import io.toast.tk.core.net.request.CommandRequest;
import io.toast.tk.core.net.request.InitInspectionRequest;
import io.toast.tk.core.net.request.TableCommandRequest;
import io.toast.tk.core.net.request.TableCommandRequestQueryCriteria;

public class TestRequestHandlersForJList {

	static String JLIST_TABLE_FIELD_NAME = "jlist";

	static JListTestFrame jlistTestFrame;

	@BeforeClass
	public static void init() {
		jlistTestFrame = new JListTestFrame();
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
		String locator = JListTestFrame.class.getName() + ":" + JLIST_TABLE_FIELD_NAME;
		List<TableCommandRequestQueryCriteria> tableCriteria = new ArrayList<TableCommandRequestQueryCriteria>();
		tableCriteria.add(new TableCommandRequestQueryCriteria("Column One", "Row2-Column1"));
		tableCriteria.add(new TableCommandRequestQueryCriteria("Column Three", "Row2-Column3"));
		TableCommandRequest buildGetInputValueRequest = buildMultiCriteriaRequest(tableCriteria, locator, idRequest);
		SwingActionRequestListener requestHandler = TestSuiteHelper.getInjector().getInstance(
			SwingActionRequestListener.class);
		FakeConnection connection = new FakeConnection();
		requestHandler.received(connection, buildGetInputValueRequest);
		try {
			Thread.sleep(2000);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static CommandRequest buildJListCommandRequest() {
		return null;// for now
	}

	public static TableCommandRequest buildMultiCriteriaRequest(
		List<TableCommandRequestQueryCriteria> tableCriteria,
		String locator,
		String idRequest) {
		return new TableCommandRequest.TableCommandRequestBuilder(idRequest).find(tableCriteria).with(locator).build();
	}

	@AfterClass
	public static void end() {
		jlistTestFrame.dispose();
	}
}

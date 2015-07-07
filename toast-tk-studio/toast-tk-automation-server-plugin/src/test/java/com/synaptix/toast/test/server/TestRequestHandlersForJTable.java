package com.synaptix.toast.test.server;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.core.net.request.InitInspectionRequest;
import com.synaptix.toast.core.net.request.TableCommandRequest;
import com.synaptix.toast.core.net.request.TableCommandRequestQueryCriteria;
import com.synaptix.toast.core.net.response.ValueResponse;
import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.SwingActionRequestListener;
import com.synaptix.toast.test.server.mock.FakeConnection;
import com.synaptix.toast.test.server.mock.TableTestFrame;

public class TestRequestHandlersForJTable {

	static String FRAME_TABLE_FIELD_NAME = "table";

	static TableTestFrame tableTestFrame;

	@BeforeClass
	public static void init() {
		tableTestFrame = new TableTestFrame();
	}

	@Before
	public void initGuiRepository() {
		InitRequestListener initRequestHandler = TestSuiteHelper.getInjector().getInstance(InitRequestListener.class);
		initRequestHandler.received(new FakeConnection(), new InitInspectionRequest());
	}

	@Test
	public void testGettingInputValue() {
		String idRequest = "fake-id";
		String expectedRow = "2";
		String locator = TableTestFrame.class.getName() + ":" + FRAME_TABLE_FIELD_NAME;
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
		assertEquals(expectedRow, ((ValueResponse) connection.result).value);
	}

	public static TableCommandRequest buildMultiCriteriaRequest(
		List<TableCommandRequestQueryCriteria> tableCriteria,
		String locator,
		String idRequest) {
		return new TableCommandRequest.TableCommandRequestBuilder(idRequest).find(tableCriteria).with(locator).build();
	}

	@AfterClass
	public static void end() {
		tableTestFrame.dispose();
	}
}

package com.synaptix.toast.test.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.adapter.swing.component.SwingButtonElement;
import com.synaptix.toast.core.adapter.AutoSwingType;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.net.request.InitInspectionRequest;
import com.synaptix.toast.core.net.response.ValueResponse;
import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.SwingActionRequestListener;
import com.synaptix.toast.test.TestSuiteHelper;
import com.synaptix.toast.test.server.mock.ButtonTestFrame;
import com.synaptix.toast.test.server.mock.FakeConnection;

public class TestRequestHandlersForButton {

	static ButtonTestFrame buttonFieldFrame;

	@BeforeClass
	public static void init() {
		buttonFieldFrame = new ButtonTestFrame();
	}

	@Before
	public void initGuiRepository() {
		InitRequestListener initRequestHandler = TestSuiteHelper.getInjector().getInstance(InitRequestListener.class);
		initRequestHandler.received(new FakeConnection(), new InitInspectionRequest());
	}

	@Test
	public void testClickResponse() {
		String idRequest = "fake-button-request-id";
		CommandRequest buildGetInputValueRequest = SwingButtonElement.buildClickRequest(
			ButtonTestFrame.class.getName() + ":buttonField",
			AutoSwingType.button.name(),
			idRequest);
		buttonFieldFrame.requestFocus();
		SwingActionRequestListener requestHandler = TestSuiteHelper.getInjector().getInstance(
			SwingActionRequestListener.class);
		FakeConnection connection = new FakeConnection();
		requestHandler.received(connection, buildGetInputValueRequest);
		try {
			Thread.sleep(1000);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(true, connection.result instanceof ValueResponse);
		assertNotNull(((ValueResponse) connection.result).b64ScreenShot);
	}

	@AfterClass
	public static void end() {
		buttonFieldFrame.dispose();
	}
}
